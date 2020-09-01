Тестовое задание для ServiceHub

В папке test хранятся тестовые проекты для проверки внешней работы проекта
Для расширения тестовых возвожностей можно написать модуль для считывания JSON из файлов и сверка с результирующими JSON значениями

Реализована поддержка горячей конфигурации сервиса получения данных о температуре.
Необходимо указать шаблон get запроса в формате http://url?par1=#user_par1#&par2=#user_par2#[new_par=par3]
где значения user_par1 и user_par2, указанные в # являются latitute и longitude
в зависимости от их расположения в get запроса на получение JSON с информацией о погоде.
Так же необходимо указать токен API и путь к интересующей информации.

В проекте использовались пакеты:
Apache Camel:
org.apache.camel:camel-servlet-starter:3.0.0-RC3
org.apache.camel:camel-jackson-starter:3.0.0-RC3
org.apache.camel:camel-swagger-java-starter:3.0.0-RC3
org.apache.camel:camel-spring-boot-starter:3.0.0-RC3
org.apache.camel:camel-http-starter:3.0.0-RC3

Spring Boot:
org.springframework.boot:spring-boot-starter-web:2.3.3.RELEASE
org.springframework:spring-tx:5.2.8.RELEASE

Apache HttpComponents:
org.apache.httpcomponents:httpclient:4.5.12

Последний модуль использовался для создания тестового ServiceA
Так же был реализован способ написание теста с помощью Apache Camel,
но не удалось обеспечить стабильность работы и правильность возвращаемых ответов от сервера
