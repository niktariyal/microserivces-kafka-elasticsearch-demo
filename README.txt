
#create kafka brokers zookeeper and schemaregistry docker containers
docker-compose -f common.yml -f kafka-cluster.yml up

#To destroy kafka containers
docker-compose -f common.yml -f kafka_cluster.yml down

#To check the status of broker and Topics if crated or not
#It will download image and create container at rutime show status and stops/exit
docker run -it --network=host confluentinc/cp-kafkacat kafkacat -L -b localhost:19092

#read/consume the tweets from topic
docker run -it --network=host confluentinc/cp-kafkacat kafkacat -C -b localhost:19092 -t twitter-topic


#to build the image
mvn build-image

#to install/download/build app skipping tests
mvn install -DskipTests


#For config server
cd  config-server-repository
ls
git init
git add .
git comm
it -am "Initial commit"