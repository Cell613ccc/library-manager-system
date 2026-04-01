package evolution.controller;

import com.alibaba.fastjson.JSONObject;
import evolution.controller.annotation.MyRequestBody;
import evolution.controller.bean.SuccessResponse;
import evolution.controller.annotation.MyPostMapping;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyServlet extends HttpServlet {
    public MyController myController;
    public Map<String, Method> apiPathToMethod;

    @Override
    public void init() throws ServletException {
        super.init();
        this.myController = new MyController();
        this.apiPathToMethod = new HashMap<>();
        for (Method method : this.myController.getClass().getDeclaredMethods()) {
            // TODO-1: 使用method.getAnnotation(..)获取@MyPostMapping注解
            MyPostMapping myPostMapping = method.getAnnotation(MyPostMapping.class);

            // TODO-2: 得到@MyPostMapping注解之后，获取其注解值，将注解值赋给apiPath
            String apiPath = myPostMapping != null ? myPostMapping.value() : null;

            this.apiPathToMethod.put(apiPath, method);
        }
    }

    @Override
    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {

        String requestUri = httpServletRequest.getRequestURI();
        System.out.println("URI: " + requestUri);

        String apiPath = requestUri.replace("/", "");
        System.out.println("网络接口路径: " + apiPath);

        Method method = this.apiPathToMethod.get(apiPath);
        SuccessResponse responseBody = null;
        if (method == null) {
            responseBody = new SuccessResponse(false, "查无此网络接口: " + apiPath);
            writeJsonResponse(responseBody, httpServletResponse);
            return;
        }

        // TODO: 使用method.getParameters()获取形参列表，再获得第一个形参(默认为请求体形参)，并将其赋给requestBodyParameter
        Parameter[] parameters = method.getParameters();
        Parameter requestBodyParameter = parameters.length > 0 ? parameters[0] : null;

        if (requestBodyParameter == null || requestBodyParameter.getAnnotation(MyRequestBody.class) == null) {
            responseBody = new SuccessResponse(false, "无法定位请求体形参");
            writeJsonResponse(responseBody, httpServletResponse);
            return;
        }

        // TODO: 使用requestBodyParameter.getType()获得请求体形参的类型，并将其赋给requestBodyType
        Class<?> requestBodyType = requestBodyParameter.getType();

        Object requestBody = this.requestBody(httpServletRequest, requestBodyType);

        // TODO: 使用method.invoke(.., .., ..)调用方法，并将结果赋给responseBody，需使用强转
        try {
            responseBody = (SuccessResponse) method.invoke(this.myController, requestBody, httpServletRequest);
        } catch (Exception e) {
            e.printStackTrace();
            responseBody = new SuccessResponse(false, "方法调用失败: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }

        this.writeJsonResponse(responseBody, httpServletResponse);
    }

    public Object requestBody(HttpServletRequest request, Class<?> requestBodyType) throws IOException {
        String requestBodyString = request.getReader().lines().collect(Collectors.joining());
        return JSONObject.parseObject(requestBodyString, requestBodyType);
    }

    public void writeJsonResponse(Object responseBody, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.print(JSONObject.toJSONString(Optional.ofNullable(responseBody).orElse(new HashMap<>())));
        writer.flush();
    }
}