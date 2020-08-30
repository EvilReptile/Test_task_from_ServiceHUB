package cham.test_project.process;

import cham.test_project.data.Coordinates;
import cham.test_project.data.ServiceA;
import cham.test_project.data.ServiceB;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UpdateData implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        // Получение входного JSON
        ServiceA json = exchange.getIn().getBody(ServiceA.class);

        // Создание объекта для выходного JSON
        ServiceB resultJson = new ServiceB();

        // Получение даты и времени
        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
        // Добавление даты и времени в JSON
        resultJson.setCreateDt(date + "T" + time + "Z");

        // Получение координат
        Coordinates coordinates = json.getCoordinates();

        // Заполнение температуры из парсера Яндекс Погоды в JSON
        resultJson.setCurrencyTemp(parseYandexWether(coordinates.getLatitude(), coordinates.getLongitude()));

        // Добавление сообщения в JSON
        resultJson.setTxt(json.getMsg());

        // Преобразование Object в StringJson и добавление в тело запроса
        ObjectMapper mapper = new ObjectMapper();
        exchange.getIn().setBody(mapper.writeValueAsString(resultJson));
    }

    private String parseYandexWether(String latitute, String longitude){
        try {
            // Создание URL сайта парсера и добавление параметров
            URL url = new URL("https://api.weather.yandex.ru/v2/forecast/?lat=" + latitute + "&lon=" + longitude + "&limit=1");

            // Инициализация подключения
            URLConnection connection = url.openConnection();

            // Добавление токена приложения
            connection.setRequestProperty("X-Yandex-API-Key", "aba068b3-37bc-4f3a-ab76-50601c4aa1bf");

            // Создание буфера для ответа и получение ответа
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = in.readLine();

            // Преобразование String в JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(result);

            // Отправляем значение fact.temp из JSON
            return actualObj.get("fact").get("temp").toString();

        // Обработка ошибок
        } catch (MalformedURLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
        return null;
    }
}
