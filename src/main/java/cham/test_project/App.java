package cham.test_project;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Инициализация Adapter
 * получает результат от ServiceA
 * подгружает данные с api.yandex.wether
 * и составляет новый JSON для ServiceB
 */
@SpringBootApplication
@ComponentScan(basePackages = "cham.test_project")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    // Настройка сервлета
    @Bean
    ServletRegistrationBean servletRegistration(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/adapter/*");
        registrationBean.setName("CamelServlet");
        return registrationBean;
    }
}
