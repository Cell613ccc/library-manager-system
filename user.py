import base64
import hashlib
import hmac
import json
import re
import secrets
import time

import os

SECRET_KEY = os.environ.get("LIBRARY_MANAGER_SECRET_KEY")
if not SECRET_KEY:
    raise RuntimeError("请设置环境变量 LIBRARY_MANAGER_SECRET_KEY 以保证 Token 有效性")
LOCKOUT_THRESHOLD = 5
LOCKOUT_DURATION = 15 * 60
TOKEN_EXPIRY_SECONDS = 24 * 60 * 60
REMEMBER_ME_EXPIRY_SECONDS = 7 * 24 * 60 * 60
PASSWORD_PATTERN = re.compile(r"^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$")
EMAIL_PATTERN = re.compile(r"^[^@\s]+@[^@\s]+\.[^@\s]+$")
PHONE_PATTERN = re.compile(r"^\+?\d{7,15}$")
ROLE_MENUS = {
    "reader": ["图书检索", "我的借阅", "个人资料"],
    "librarian": ["借还书处理", "图书上架/下架"],
    "admin": ["用户管理", "角色分配", "系统日志"],
}


class AuthError(Exception):
    pass


class AuthManager:
    def __init__(self):
        self.users = {}
        self.users_by_phone = {}
        self.user_roles = {}
        self.failed_attempts = {}
        self.lockouts = {}
        self.revoked_tokens = set()
        self.next_user_id = 1

    def _hash_password(self, password: str) -> str:
        salt = secrets.token_bytes(16)
        dk = hashlib.pbkdf2_hmac("sha256", password.encode("utf-8"), salt, 100_000)
        return base64.urlsafe_b64encode(salt + dk).decode("utf-8")

    def _check_password(self, password: str, stored_hash: str) -> bool:
        raw = base64.urlsafe_b64decode(stored_hash.encode("utf-8"))
        salt, dk = raw[:16], raw[16:]
        candidate = hashlib.pbkdf2_hmac("sha256", password.encode("utf-8"), salt, 100_000)
        return hmac.compare_digest(candidate, dk)

    def _validate_email(self, email: str) -> None:
        if not EMAIL_PATTERN.match(email):
            raise AuthError("邮箱格式不合法")

    def _validate_phone(self, phone: str) -> None:
        if not PHONE_PATTERN.match(phone):
            raise AuthError("手机号格式不合法")

    def _validate_password(self, password: str) -> None:
        if not PASSWORD_PATTERN.match(password):
            raise AuthError("密码必须至少8位且包含字母和数字")

    def _create_token(self, user_id: int, roles: list, expires_in: int) -> str:
        payload = {
            "uid": user_id,
            "roles": roles,
            "iat": int(time.time()),
            "exp": int(time.time()) + expires_in,
        }
        payload_bytes = json.dumps(payload, separators=(",", ":")).encode("utf-8")
        payload_b64 = base64.urlsafe_b64encode(payload_bytes).rstrip(b"=").decode("utf-8")
        signature = hmac.new(SECRET_KEY.encode("utf-8"), payload_b64.encode("utf-8"), hashlib.sha256).digest()
        signature_b64 = base64.urlsafe_b64encode(signature).rstrip(b"=").decode("utf-8")
        return f"{payload_b64}.{signature_b64}"

    def _decode_token(self, token: str) -> dict:
        if token in self.revoked_tokens:
            raise AuthError("Token 已失效")
        try:
            payload_b64, signature_b64 = token.split('.')
        except ValueError:
            raise AuthError("无效的 Token")
        expected = hmac.new(SECRET_KEY.encode("utf-8"), payload_b64.encode("utf-8"), hashlib.sha256).digest()
        actual = base64.urlsafe_b64decode(signature_b64 + '=' * (-len(signature_b64) % 4))
        if not hmac.compare_digest(expected, actual):
            raise AuthError("Token 签名不匹配")
        payload_json = base64.urlsafe_b64decode(payload_b64 + '=' * (-len(payload_b64) % 4))
        payload = json.loads(payload_json.decode("utf-8"))
        if payload.get("exp", 0) < int(time.time()):
            raise AuthError("Token 已过期")
        return payload

    def register(self, email: str, phone: str, password: str) -> dict:
        self._validate_email(email)
        self._validate_phone(phone)
        self._validate_password(password)
        if email in self.users:
            raise AuthError("邮箱已注册")
        if phone in self.users_by_phone:
            raise AuthError("手机号已注册")
        user_id = self.next_user_id
        self.next_user_id += 1
        user = {
            "id": user_id,
            "email": email,
            "phone": phone,
            "password_hash": self._hash_password(password),
            "created_at": int(time.time()),
        }
        self.lockouts[user_id] = None
        self.users_by_phone[phone] = user
        self.user_roles[user_id] = ["reader"]
        self.failed_attempts[user_id] = 0
        self.lockouts[user_id] = None
        return {"user_id": user_id, "roles": ["reader"]}

    def _find_user(self, identifier: str) -> dict:
        if EMAIL_PATTERN.match(identifier):
            return self.users.get(identifier)
        if PHONE_PATTERN.match(identifier):
            return self.users_by_phone.get(identifier)
        raise AuthError("登录凭证格式不支持")

    def login(self, identifier: str, password: str, remember_me: bool = False) -> dict:
        lockout_until = self.lockouts.get(user_id)
        if lockout_until is not None and lockout_until > time.time():
            raise AuthError("账户已锁定，请稍后再试")
            raise AuthError("用户不存在")
        lockout_until = self.lockouts.get(user_id, None)
        if lockout_until is not None and lockout_until > time.time():
            raise AuthError("账户已锁定，请稍后再试")
            raise AuthError("账户已锁定，请稍后再试")
        if not self._check_password(password, user["password_hash"]):
            if self.failed_attempts[user_id] >= LOCKOUT_THRESHOLD:
                self.lockouts[user_id] = time.time() + LOCKOUT_DURATION
                self.failed_attempts[user_id] = 0
                raise AuthError("连续输错5次，账户锁定15分钟")
            raise AuthError("密码错误")
        self.failed_attempts[user_id] = 0
        self.lockouts[user_id] = None
        self.failed_attempts[user_id] = 0
        expires_in = REMEMBER_ME_EXPIRY_SECONDS if remember_me else TOKEN_EXPIRY_SECONDS
        roles = self.user_roles.get(user_id, ["reader"])
        token = self._create_token(user_id, roles, expires_in)
        return {"token": token, "roles": roles, "expires_in": expires_in}

    def logout(self, token: str) -> None:
        self.revoked_tokens.add(token)

    def require_role(self, token: str, role: str) -> dict:
        payload = self._decode_token(token)
        if role not in payload.get("roles", []):
            raise AuthError("权限不足")
    def __init__(self):
        self.users = {}
        self.users_by_phone = {}
        self.user_roles = {}
        self.failed_attempts = {}
        self.lockouts = {}
        self.revoked_tokens = set()
        self.next_user_id = 1
        self._menu_cache = {}  # token: (menu, expiry)

    def get_menu_for_token(self, token: str) -> list:
        now = int(time.time())
        cached = self._menu_cache.get(token)
        if cached and cached[1] > now:
            return cached[0]
        payload = self._decode_token(token)
        roles = payload.get("roles", [])
        menus = set(ROLE_MENUS["reader"])
        if "librarian" in roles or "admin" in roles:
            menus.update(ROLE_MENUS["librarian"])
        if "admin" in roles:
            menus.update(ROLE_MENUS["admin"])
        menu_list = sorted(menus)
        # Cache menu until token expiry
if __name__ == "__main__":
    print("这是用户认证与权限管理模块的代码示例。")
    auth = AuthManager()
    auth.register("reader@example.com", "+8613800000000", "Password123")
    session = auth.login("reader@example.com", "Password123")
    print("登录成功", session)
    print("菜单", auth.get_menu_for_token(session["token"]))
    auth.assign_role("reader@example.com", "librarian")
    session2 = auth.login("reader@example.com", "Password123")
    print("角色升级后菜单", auth.get_menu_for_token(session2["token"]))
    auth.logout(session2["token"])
    try:
        auth._decode_token(session2["token"])
    except AuthError as err:
        print("已登出，访问失败：", err)
        self.user_roles[user_id] = list(roles)


if __name__ == "__main__":
    auth = AuthManager()
    auth.register("reader@example.com", "+8613800000000", "Password123")
    session = auth.login("reader@example.com", "Password123")
    print("登录成功", session)
    print("菜单", auth.get_menu_for_token(session["token"]))
    auth.assign_role("reader@example.com", "librarian")
    session2 = auth.login("reader@example.com", "Password123")
    print("角色升级后菜单", auth.get_menu_for_token(session2["token"]))
    auth.logout(session2["token"])
    try:
        auth._decode_token(session2["token"])
    except AuthError as err:
        print("已登出，访问失败：", err)