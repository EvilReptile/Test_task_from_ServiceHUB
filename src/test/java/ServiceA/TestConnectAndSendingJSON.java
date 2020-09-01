package ServiceA;

import cham.test_project.controller.Adapter;
import cham.test_project.data.ServiceA;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeException;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.Constants;
import org.apache.camel.util.json.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.juli.logging.LogFactory;

import java.io.*;

/**
 * Инициализация ServiceA
 * на выход подается тестовый JSON,
 * на вход получаем результирующий код
 */
public class TestConnectAndSendingJSON {
    public static void main(String[] args) throws IOException {
        /* Тестовый JSON
        {
            "msg": "Hello",
            "lng": "ru",
            "coordinates": {
                "latitude": "54.35",
                "longitude": "52.52"
            }
        }
        */
        // Создаем тестовый JSON для отправки на сервер
        JsonObject json =  new JsonObject();
        json.put("msg", "Hello");
        json.put("lng", "ru");

        // Создаем внутренний JSON
        JsonObject coord = new JsonObject();
        coord.put("latitude", "54.53");
        coord.put("longitude", "52.52");
        json.put("coordinates", coord);

        // Инициализируем подключение к серверу
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://localhost:8080/adapter/");

        // Загружаем JSON в контейнер
        StringEntity params =new StringEntity(json.toJson());

        // Отправляем JSON
        httpPost.setEntity(params);
        HttpResponse response = httpClient.execute(httpPost);

        // Получаем статус ответа
        String res = response.getStatusLine() + "\n" + new String(response.getEntity().getContent().readAllBytes());
        System.out.println(res);
        //test();
    }

    public static void test() {
        CamelContext context = new DefaultCamelContext();
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("timer:fixedRate=true&period=4000")
                            .routeId("Test")
                            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) throws Exception {
                                    JsonObject json =  new JsonObject();
                                    json.put("msg", "Hello");
                                    json.put("lng", "ru");

                                    // Создаем внутренний JSON
                                    JsonObject coord = new JsonObject();
                                    coord.put("latitude", "54.53");
                                    coord.put("longitude", "52.52");
                                    json.put("coordinates", coord);

                                    System.out.println(json.toJson());
                                    exchange.getIn().setBody(json.toJson(), ServiceA.class);
                                }
                            })
                            .to("http://localhost:8080/adapter/")
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) throws Exception {
                                    System.out.println(exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, String.class));
                                    System.out.println(exchange.getIn().getBody(String.class));
                                }
                            });
                }
            });

            context.start();
            Thread.sleep(4000);
            context.stop();
        }catch (Exception e){
            e.fillInStackTrace();
        }



    }
}