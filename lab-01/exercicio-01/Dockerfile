FROM ubuntu:26.04
#  FROM ubuntu:  cntrl + barra de espaço para abrir as tags, não é bom usar o latest, é melhor usar o nome da versão especifica
  


WORKDIR /app

RUN apt-get update && \
    apt-get install -y figlet nyancat toilet && \ 
    rm -rf /var/lib/apt/lists/*

COPY mensagem.txt /app/

CMD ["toilet", "-f", "smmono9", "--filter", "gay",  "figueirense"]



