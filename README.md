[![Build Status](https://travis-ci.org/samaitra/streamers.svg?branch=master)](https://travis-ci.org/samaitra/streamers)

Apache IgniteSink offers a streaming connector to inject Flink data into the Ignite cache. The sink emits its input data to the Ignite cache. The key feature to note is the performance and scale both Apache Flink and Apache Ignite offer. Apache Flink can process unbounded and bounded data sets and has been designed to run stateful streaming applications at scale. Application computation is distributed and concurrently executed in clusters. Apache Flink is also optimized for local state access for tasks and does checkpointing of local state for durability. Apache Ignite provides streaming capabilities that allow data ingestion at high scale in its in-memory data grid.

In this post, we will discuss how we can build a data streaming application using Apache Flink and Apache Ignite. Building a data streaming application offers the benefit of ingesting large finite and infinite volumes of data in an optimized and fault tolerant way into the Ignite cluster. The data ingestion rate is very high and can scale up to millions of events per seconds.

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

### Dispatcher: Overview

![Flink Dispatcher Overview](https://github.com/samaitra/streamers/raw/master/resources/flink_job.png) 

You can also verify that the system is running by checking the log files in the logs directory:
```
$ tail log/flink-*-standalonesession-*.log
```

### Download kafka 

Download a binary from the downloads page (https://kafka.apache.org/downloads). You can pick Apache Kafka `0.10.2.2` version with `scala 2.11`.

### Start zookeeper server
```
$./bin/zookeeper-server-start.sh ./config/zookeeper.properties
```

### Start broker
```
./bin/kafka-server-start.sh ./config/server.properties 
```

### Create topic “mytopic”
```
$ ./bin/kafka-topics.sh --create --topic mytopic --zookeeper localhost:2181 --partitions 1 --replication-factor 1
```

### Describe topic "mytopic"

```
$ ./bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic mytopic
```

### Produce something into the topic (write something and hit enter)
```
$ ./bin/kafka-console-producer.sh --topic mytopic --broker-list localhost:9092
```

### Consume from the topic using the console producer
```
$ ./bin/kafka-console-consumer.sh --topic mytopic --zookeeper localhost:2181
```

### Clone Apache Ignite 

```
$ git clone https://github.com/apache/ignite
```

### Build Apache Ignite 

```
$ mvn clean package install -DskipTests
```


### Build the Flink program :
```
$ mvn clean package
```

### Submit the Flink program :
```
$ ./bin/flink run streamers-1.0-SNAPSHOT.jar
```

### Produce something into the topic (write something and hit enter)
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

### Ignite Web Console

#### Ignite Web Console Build Instructions

1. Install MongoDB (version >=3.2.0 <=3.4.15) using instructions from http://docs.mongodb.org/manual/installation.
2. Install Node.js (version >=8.0.0) using installer from https://nodejs.org/en/download/current for your OS.
3. Change directory to 'modules/web-console/backend' and
 run "npm install --no-optional" for download backend dependencies.
4. Change directory to 'modules/web-console/frontend' and
 run "npm install --no-optional" for download frontend dependencies.
5. Build ignite-web-agent module follow instructions from 'modules/web-console/web-agent/README.txt'.
6. Copy ignite-web-agent-<version>.zip from 'modules/web-console/web-agent/target'
 to 'modules/web-console/backend/agent_dists' folder.
7. Unzip ignite-web-agent-<version>.zip in 'modules/web-console/backend/agent_dists'
8. run './ignite-web-agent.sh' inside ignite-web-agent-<version> folder 

Steps 1 - 4 should be executed once.

#### Ignite Web Console Run In Development Mode

1. Configure MongoDB to run as service or in terminal change dir to $MONGO_INSTALL_DIR/server/3.2/bin
  and start MongoDB by executing "mongod".

2. In new terminal change directory to 'modules/web-console/backend'.
   If needed run "npm install --no-optional" (if dependencies changed) and run "npm start" to start backend.

3. In new terminal change directory to 'modules/web-console/frontend'.
  If needed run "npm install --no-optional" (if dependencies changed) and start webpack in development mode "npm run dev".

4. In browser open: http://localhost:9000

Web console can be used to scan cache and view all the cache contents. 

### To stop Flink when you’re done type:
```
$ ./bin/stop-cluster.sh
```
