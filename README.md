Тестовое задание для ServiceHub

В папке test хранятся тестовые проекты для проверки внешней работы проекта
Для расширения тестовых возвожностей можно написать модуль для считывания JSON из файлов и сверка с результирующими JSON значениями

Так как у каждого сервиса разные API писать конфигурационный файл для этого сложно, но при необходимости можно реализовать,
но потребуется больше времени
Так же можно реализовать получение значений из всех API и указание среднего по всем сервисам

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
Так же можно было перевести ServiceA на технологию Apache Camel
