package ServiceB;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import java.util.Collections;

/**
 * Инициализация ServiceB
 * на выходе полученый результат выводится в консоль
 */
@SpringBootApplication
@ComponentScan(basePackages = "ServiceB")
public class TestConnectAndReceivingJSON {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TestConnectAndReceivingJSON.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8085"));
        app.run(args);
    }

    // Настройка сервлета
    @Bean
    ServletRegistrationBean servletRegistration(){
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/service_b/*");
        registrationBean.setName("CamelServlet");
        return registrationBean;
    }

}
