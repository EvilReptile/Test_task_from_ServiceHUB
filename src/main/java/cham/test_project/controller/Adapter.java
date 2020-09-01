package cham.test_project.controller;

import cham.test_project.data.ServiceA;
import cham.test_project.data.ServiceB;
import cham.test_project.process.UpdateData;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class Adapter extends RouteBuilder {
    @Override
    public void configure() {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        // Получаем POST запрос данные по URL /adapter/
        rest()
                .post()
                    .type(ServiceA.class)
                    .to("direct:transform");

        // Фильтрация и обработка принятых данных
        from("direct:transform")
                .choice()
                // Проверка на пустоту, возврат 204 ошибки, если msg пустой
                .when().simple("${body.msg} == ''")
                    .transform().constant("OK")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
                // Проверка на тело lng, если содержит ru, то обрабатываем и отправялем
                .when().simple("${body.lng} == 'ru'")
                        .process(new UpdateData())
                        .to("direct:output")
                // Если условия не прошли, то возвращаем код 202, но не обрабатываем
                .otherwise()
                    .transform().constant("OK")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202));

        // Отправка обработанных данных и возврат кода 202
        from("direct:output")
                .choice()
                // Проверка на наличие данных о погоде, если нету, то возвращаем код 424 внутренняя ошибка сервиса
                .when().simple("${body} == ''")
                    .transform().constant("Incorrect operation of the weather service")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(424))
                // Если проверка пройдена - отправляем JSON на ServerB
                .otherwise()
                    .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                    .to("http://localhost:8085/service_b/?bridgeEndpoint=true")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202));

    }


}
