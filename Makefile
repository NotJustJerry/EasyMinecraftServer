NAME = minecraft-server

build:
    docker build -t ${NAME} .
run:
    docker run -it -p 25565:25565 ${NAME}