# streamers
A collection of streaming application

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

### Run the Example
Now, we are going to run this Flink application. It will read text from a socket and once every 5 seconds print the number of occurrences of each distinct word during the previous 5 seconds, i.e. a tumbling window of processing time, as long as words are floating in.

First of all, we use netcat to start local server via
```
$ nc -l 9000
```

### Submit the Flink program:
```
$ ./bin/flink run streamers-1.0-SNAPSHOT.jar --port 9000
```

Starting execution of program
The program connects to the socket and waits for input. You can check the web interface to verify that the job is running as expected:

Dispatcher: Overview (cont'd) Dispatcher: Running Jobs
Words are counted in time windows of 5 seconds (processing time, tumbling windows) and are printed to stdout. Monitor the TaskManager’s output file and write some text in nc (input is sent to Flink line by line after hitting ):
```
$ nc -l 9000
lorem ipsum
ipsum ipsum ipsum
bye
```

The .out file will print the counts at the end of each time window as long as words are floating in, e.g.:
```
$ tail -f log/flink-*-taskexecutor-*.out
lorem : 1
bye : 1
ipsum : 4
```

### To stop Flink when you’re done type:
```
$ ./bin/stop-cluster.sh
```