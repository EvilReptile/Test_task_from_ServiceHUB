package ServiceB;

import cham.test_project.data.ServiceB;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class TestController extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        // Прием POST запроса с API /service_b/
        rest()
                .post()
                    .type(ServiceB.class)
                    .to("direct:console");

        // Обработка полученых данных
        from("direct:console")
                .log("Значение txt - ${body.txt}, Значение createDt - ${body.createDt}, Значение currencyTemp - ${body.currencyTemp}")
                .transform().constant("OK");

    }
}
