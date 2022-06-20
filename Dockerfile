FROM ubuntu

RUN echo 'Asia/Shanghai' >/etc/timezone
RUN apt-get update && apt-get upgrade
RUN add-apt-repository ppa:linuxuprising/java -y
RUN apt-get install wget
RUN apt update
RUN apt-get install oracle-java17-installer oracle-java17-set-default
RUN ECHO "y\n"
RUN mkdir /www
RUN mkdir /www/minecraft
COPY ./target/minecraft-1.0-SNAPSHOT.jar /www/minecraft/minecraft-server.jar
COPY server /www/minecraft

EXPOSE 25565
#ENTRYPOINT ["java","-jar","/www/minecraft/minecraft-server.jar","/www/minecraft/server.jar"]