./mvnw clean package -DskipTests

docker build . -t jonathonclifford/mm-models-service
