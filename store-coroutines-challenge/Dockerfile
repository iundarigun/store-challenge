FROM openjdk:11 AS BUILD_IMAGE
ENV APP_HOME=/root/dev/app
RUN mkdir -p $APP_HOME
WORKDIR $APP_HOME
COPY settings.gradle.kts build.gradle.kts gradlew gradlew.bat custom-detekt.yml $APP_HOME/
COPY gradle $APP_HOME/gradle
COPY src src
RUN ./gradlew build -x test

FROM openjdk:11-jre
WORKDIR /root/
COPY --from=BUILD_IMAGE /root/dev/app/build/libs/store.jar .
EXPOSE 1980
CMD ["java","-jar","store.jar"]