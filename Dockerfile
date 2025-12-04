FROM eclipse-temurin:17-jdk-alpine

# Alpine tabanlı imaj daha küçüktür ve hızlı iner/çalışır
WORKDIR /app

# Eğer target klasöründe birden fazla jar varsa build patlamasın diye
# Genelde build sırasında tek bir final jar oluştuğundan emin olmalısın.
# Buradaki komut senin yazdığınla aynı mantıkta, sadece base image'i hafiflettim.
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]