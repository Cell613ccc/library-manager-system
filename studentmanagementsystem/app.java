package studentmanagementsystem;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
public class app{
    static Scanner sc=new Scanner(System.in);
    public static void main(String[] args) {
        ArrayList<user> list=new ArrayList<>();
        while(true){
        System.out.println("欢迎来到学生管理系统");
        System.out.println("请输入你的选择：1:登录2:注册3：忘记密码");
        String select=sc.next();
        switch(select){
            case"1"-> log(list);
            case"2"-> register(list);
            case"3"-> System.out.println();
            case"4"-> System.out.println();
            default-> System.out.println("无此选项");
        }
    }
    }
    public static void register(ArrayList<user> list){
        user u=new user();
        setUsername(list,u);
        setPassword(u);
        setIdentity(u);
        setPhone(u);
        list.add(u);
        System.out.println("注册成功");
    }
    public static int getIndex(ArrayList<user> list,String username){
        for(int i=0;i<list.size();i++){
            user u=list.get(i);
            if(u.getUsername().equals(username)){
                return i;
            }
        }
        return -1;
    }
    public static void setUsername(ArrayList<user> list,user u){
        loop:while(true){
        System.out.println("请输入用户名");
        String username=sc.next();
        int length=username.length();
        if(length<3||length>15){
            System.out.println("用户名长度不正确，重新输入");
            continue;
        }
        for(int i=0;i<length;i++){
            char alph=username.charAt(i);
            if(!(('a'<=alph && alph<='z')||('A'<=alph && alph<='Z')||('0'<=alph && alph<='9'))){
                System.out.println("用户名需是数字加字母组合，重新输入");
                continue loop;
            }
        }
        int count=0;
        for(int j=0;j<length;j++){
            char alph1=username.charAt(j);
            if(('a'<=alph1 && alph1<='z')||('A'<=alph1 && alph1<='Z')){
                count++;
            }
        }
        if(count==0){
            System.out.println("用户名不能是纯数字，重新输入");
            continue;
        }
        int index=getIndex(list,username);
        if(index!=-1){
            System.out.println("用户名已存在，重新输入");
        }
        u.setUsername(username);
        System.out.println("用户名设置成功");
        break;
    }
}
    public static void setPassword(user u){
        while(true){System.out.println("请设置密码");
        String pass1=sc.next();
        System.out.println("请再次设置密码");
        String pass2=sc.next();
        if(pass1.equals(pass2)){
            u.setPassword(pass1);
            System.out.println("密码设置成功");
            break;
        }
        else{
            System.out.println("前后密码不一致请重新设置");
            continue;
        }
    }
}
    public static void setIdentity(user u){
        loop:while(true){
        System.out.println("请输入身份证");
        String identity=sc.next();
        if(identity.length()!=18){
            System.out.println("身份证长度不正常，重新输入");
            continue;
        }
        if(identity.startsWith("0")){
            System.out.println("身份证格式首位不能为0，重新输入");
            continue;
        }
        for(int i=0;i<(identity.length()-1);i++){
            char c=identity.charAt(i);
            if(!(c>='0'&&c<='9')){
                System.out.println("身份证前17位不能出现字符，重新输入");
                continue loop;
            }
        }
        char c=identity.charAt(17);
        if(!((c>='0'&&c<='9')||(c=='X')||(c=='x'))){
            System.out.println("身份证最后一位只能是数字或者Xx，重新输入");
            continue;
        }
        else{
            u.setIdentity(identity);
            System.out.println("身份证设置成功");
            break;
        }
    }
}
    public static void setPhone(user u){
        loop:while(true){
            System.out.println("请输入手机号码");
            String phone=sc.next();
            int length=phone.length();
            if (length!=11){
                System.out.println("手机号长度不正确。重新输入");
                continue;
            }
            if(phone.startsWith("0")){
                System.out.println("手机号开头不能是0.重新输入");
                continue;
            }
            for(int i=0;i<length;i++){
                if(!(phone.charAt(i)>='0'&&phone.charAt(i)<='9')){
                    System.out.println("手机号不能有其他字符，重新输入");
                    continue loop;
                }
            }
            u.setPhonenumber(phone);
            System.out.println("手机号设置成功");
            break;
        }
    }
    public static void log(ArrayList<user> list){
        for(int i=1;i<4;i++){
        System.out.println("请输入用户名");
        String username=sc.next();
        int jdg=getIndex(list,username);
        if(jdg<0){
            System.out.println("用户未注册请先注册");
            return;
        }
        System.out.println("请输入密码");
        String password=sc.next();
        while(true){
        String rcode=createCode();
        System.out.println("验证码是"+rcode);
        System.out.println("请输入验证码");
        String code=sc.next();
        if(rcode.equalsIgnoreCase(code)){
            System.out.println("验证码正确");
            break;
        }
        System.out.println("验证码错误.请重新输入验证码");
    }
       user u=new user(username,password,null,null);
       boolean pass=checkUsernameAndPassword(list,u,jdg);
       if(pass){
        System.out.println("登录成功");
        return;
       }
       if(i==3){
        System.out.println("密码或用户名错误，账号已被锁定");
        return;
       }
       System.out.println("密码或者用户名错误，重新输入，还剩下"+(4-i)+"次机会");
    }
    }
    public static String createCode(){
        ArrayList<Character> list=new ArrayList<>();
        Random r=new Random();
        for(int i=0;i<26;i++){
            list.add((char)('a'+i));
            list.add((char)('A'+i));
        }
        StringBuilder sb=new StringBuilder();
        for(int j=0;j<4;j++){
            int index=r.nextInt(list.size());
            sb.append(list.get(index));
        }
        int num=r.nextInt(10);
        sb.append(num);
        char arr[]=sb.toString().toCharArray();
        int ranindex=r.nextInt(arr.length);
        char c=arr[arr.length-1];
        arr[arr.length-1]=arr[ranindex];
        arr[ranindex]=c;
        return new String(arr);
    }
    public static boolean checkUsernameAndPassword(ArrayList<user> list,user u,int index){
        if(list.get(index).getUsername().equals(u.getUsername())&&list.get(index).getPassword().equals(u.getPassword())){
            return true;
        }
        return false;
    }
}
