# Estágio 1: Build - Usamos uma imagem com Gradle e Java para compilar o projeto
FROM gradle:8.8.0-jdk21-jammy AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
# Executa o build do Gradle para gerar o arquivo .jar
RUN gradle build --no-daemon

# Estágio 2: Run - Usamos uma imagem Java mínima para rodar a aplicação
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copia apenas o .jar compilado do estágio de build para a imagem final
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
# Expõe a porta que a aplicação vai usar
EXPOSE 8080
# Comando para executar a aplicação quando o contêiner iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]