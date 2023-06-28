FROM openjdk:17 as builder

WORKDIR /app
ARG JAR_FILE=demo*-boot.jar
COPY ./build/libs/${JAR_FILE} ./app.jar

FROM busybox:1.36.0-musl as healthcheck

FROM gcr.io/distroless/java17-debian11:nonroot
WORKDIR /app
COPY --from=builder /app/app.jar ./
COPY --from=healthcheck /bin/wget /usr/bin/wget

EXPOSE 8080
ENV JDK_JAVA_OPTIONS="-XshowSettings:vm -XX:+UseContainerSupport -XX:MaxRAMPercentage=90 -Djava.awt.headless=true -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom"
CMD ["app.jar"]
HEALTHCHECK --timeout=10s --start-period=5s --retries=3 \
    CMD /usr/bin/wget --no-verbose --tries=1 --spider 'http://127.0.0.1/actuator/health' || exit 1
