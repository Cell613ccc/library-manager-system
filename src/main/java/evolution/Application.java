package evolution;

import evolution.controller.MyServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    @Bean
    public ServletRegistrationBean<MyServlet> dynamicServlet() {
        return new ServletRegistrationBean<>(new MyServlet(),"/*");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}