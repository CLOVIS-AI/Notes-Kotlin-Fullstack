FROM alpine:latest as builder

COPY backend.tar /home/work/app.tar
WORKDIR /home/work
RUN tar -xf app.tar
RUN mkdir -p extracted
RUN mv backend*/* extracted

FROM alpine:latest

RUN apk add --no-cache openjdk17-jre-headless
COPY --from=builder /home/work/extracted /opt/app

WORKDIR /opt/app
EXPOSE 9000
ENTRYPOINT [ "/opt/app/bin/backend" ]
