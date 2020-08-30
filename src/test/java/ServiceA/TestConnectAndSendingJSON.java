package ServiceA;

import org.apache.camel.util.json.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
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
        System.out.println(response.getStatusLine());

    }
}
