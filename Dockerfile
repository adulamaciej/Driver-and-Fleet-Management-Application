# Używamy jednego obrazu dla uproszczenia
FROM openjdk:23

WORKDIR /app

# Kopiowanie plików projektu i już zbudowanego jar-a
COPY target/*.jar app.jar

# Ekspozycja portu
EXPOSE 8080

# Uruchomienie aplikacji
CMD ["java", "-jar", "app.jar"]