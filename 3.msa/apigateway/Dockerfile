FROM eclipse-temurin:17-jdk AS stage1
WORKDIR /app
# 폴더는 폴더명을 써주어야 됨. gradle을 gradle로 복사하겟다.
COPY gradle gradle
COPY src src
# . : 현재 폴더에 COPY
COPY build.gradle . 
COPY gradlew .
COPY settings.gradle .
# 실행 권한 부여
RUN chmod +x gradlew
RUN ./gradlew bootJar

FROM eclipse-temurin:17-jre AS stage2
WORKDIR /app
COPY --from=stage1 /app/build/libs/*jar ordersystem.jar
ENTRYPOINT ["java", "-jar", "ordersystem.jar"]

# 도커이미지 빌드
# docker build -t jeonghunyhoon/myordersystem:latest .

# 도커이미지 push
# docker push jeonghunyhoon/myordersystem:latest

# 도커컨테이너 실행 (host.do1cker.internal -> 이자리가 RDS 주소가 됨)
# docker run --name myspring -d -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:mariadb://host.docker.internal:3306/ordersystem?useSSL=true -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=test1234 -e SPRING_REDIS_HOST=host.docker.internal jeonghunyhoon/myordersystem:latest