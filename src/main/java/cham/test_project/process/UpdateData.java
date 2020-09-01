package cham.test_project.process;

import cham.test_project.controller.Adapter;
import cham.test_project.data.Coordinates;
import cham.test_project.data.ServiceA;
import cham.test_project.data.ServiceB;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static org.apache.camel.language.constant.ConstantLanguage.constant;

public class UpdateData implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {

        // Получение списка параметров
        HashMap<String, String> res = getConfig();

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

        // Создание URL ссылки для парсера
        String[] params = res.get("url").split("#");
        String url = "";
        for(String param: params)
            if (param.equals("latitute"))
                url += coordinates.getLatitude();
            else if(param.equals("longitude"))
                url += coordinates.getLongitude();
            else
                url += param;

        // Заполнение температуры из парсера Яндекс Погоды в JSON
        String temp = parseWether(res.get("token_name"), res.get("token"), url, res.get("data_path"));

        // Если вернулось пустое значение температуры, значит ошибка в config файле и данные не валидны, отправляем пустое сообщение
        if (temp.equals("")) {
            exchange.getIn().setBody("");
            return;
        }
        resultJson.setCurrencyTemp(parseWether(res.get("token_name"), res.get("token"), url, res.get("data_path")));

        // Добавление сообщения в JSON
        resultJson.setTxt(json.getMsg());

        // Преобразование Object в StringJson и добавление в тело запроса
        ObjectMapper mapper = new ObjectMapper();
        exchange.getIn().setBody(mapper.writeValueAsString(resultJson));
    }

    // Получение конфигурации для парсинга данных с сайта погоды
    private HashMap<String, String> getConfig(){
        // Инициализация списка с настройками
        HashMap<String, String> result = new HashMap<>();

        try {
            // Получение данных из файла
            String json = "";
            FileInputStream fis = new FileInputStream("config.txt");
            int i = -1;
            while ((i = fis.read()) != -1)
                json += (char)i;

            // Преобразование файла с параметрами в JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(json).get("weather_api");

            // Получение параметров
            result.put("token_name", actualObj.get("token_name").toString().split("\"")[1]);
            result.put("token", actualObj.get("token").toString().split("\"")[1]);
            result.put("url", actualObj.get("url").toString().split("\"")[1]);
            result.put("data_path", actualObj.get("data_path").toString().split("\"")[1]);

        // Обработка ошибок
        }catch (IOException e){
            LogFactory.getLog(Adapter.class).error(e.getMessage());
        }

        return result;
    }

    private String parseWether(String token_name, String token, String link, String data_path){
        try {
            // Создание URL сайта парсера и добавление параметров
            URL url = new URL(link);

            // Инициализация подключения
            URLConnection connection = url.openConnection();

            // Добавление токена приложения
            if(!token.isEmpty() && !token_name.isEmpty())
                connection.setRequestProperty(token_name, token);

            // Создание буфера для ответа и получение ответа
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = in.readLine();

            // Преобразование String в JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(result);

            // Отправляем значение fact.temp из JSON
            String[] path = data_path.split(" ");

            for (String node: path)
                actualObj = actualObj.get(node);

            return actualObj.toString();

        // Обработка ошибок
        } catch (MalformedURLException e) {
            LogFactory.getLog(Adapter.class).error("Invalid url: " + e.getMessage());
        } catch (IOException e) {
            LogFactory.getLog(Adapter.class).error(e.getMessage());
        }
        return "";
    }
}
