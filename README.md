# streamers
A collection of streaming application

### Stream processing topology

![Stream processing topology](https://github.com/samaitra/streamers/raw/master/resources/streamers.png) 

### Setup: Download and Start Flink

Download a binary from the downloads page. You can pick any Hadoop/Scala combination you like. If you plan to just use the local file system, any Hadoop version will work fine.
Go to the download directory.

### Unpack the downloaded archive.
```
$ cd ~/Downloads        # Go to download directory
$ tar xzf flink-*.tgz   # Unpack the downloaded archive
$ cd flink-1.5.0
```

### Start a Local Flink Cluster
```
$ ./bin/start-cluster.sh  # Start Flink
```
Check the Dispatcher’s web frontend at http://localhost:8081 and make sure everything is up and running. The web frontend should report a single available TaskManager instance.

Dispatcher: Overview

You can also verify that the system is running by checking the log files in the logs directory:
```
$ tail log/flink-*-standalonesession-*.log
```

### Download kafka 

### start zookeeper server
```
$./bin/zookeeper-server-start.sh ./config/zookeeper.properties
```

### start broker
```
./bin/kafka-server-start.sh ./config/server.properties 
```

### create topic “mytopic”
```
$ ./bin/kafka-topics.sh --create --topic mytopic --zookeeper localhost:2181 --partitions 1 --replication-factor 1
```

### Describe topic "mytopic"

```
$ bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic mytopic
```

### produce something into the topic (write something and hit enter)
```
$ ./bin/kafka-console-producer.sh --topic mytopic --broker-list localhost:9092
```

### consume from the topic using the console producer
```
$ ./bin/kafka-console-consumer.sh --topic mytopic --zookeeper localhost:2181
```

### Build the Flink program :
```
$ mvn clean package
```

### Submit the Flink program :
```
$ ./bin/flink run streamers-1.0-SNAPSHOT.jar
```

### produce something into the topic (write something and hit enter)
```
$ ./bin/kafka-console-producer.sh --topic mytopic --broker-list localhost:9092
```

The .out file will print the counts at the end of each time window as long as words are floating in, e.g.:
```
$ tail -f log/flink-*-taskexecutor-*.out
lorem : 1
bye : 1
ipsum : 4
```

### Ignite rest service
To check the cache key values you can use the Ignite rest service 

```
$ curl -X GET http://localhost:8080/ignite\?cmd\=getall\&k1\=jam\&cacheName\=testCache
```

### Scan cache 
To check all the keys from an Ignite cache the following rest service can be used
```
$ curl -X GET http://localhost:8080/ignite?cmd=qryscanexe&pageSize=10&cacheName=testCache
```

### To stop Flink when you’re done type:
```
$ ./bin/stop-cluster.sh
```