mvn -fn clean package

sudo docker build -t pdimas12/scc-app ..

sudo docker push pdimas12/scc-app