package com.samaitra;

import java.util.HashMap;
import java.util.Map;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.ignite.sink.flink.IgniteSink;

public class SocketWindowWordCount {


    public static void main(String[] args) throws Exception {
        /** Ignite test configuration file. */
        final String GRID_CONF_FILE = "/Users/saikat/git/streamers/src/main/resources/example-ignite.xml";

        IgniteSink igniteSink = new IgniteSink("testCache", GRID_CONF_FILE);

        igniteSink.setAllowOverwrite(true);

        igniteSink.setAutoFlushFrequency(5L);

        Configuration p = new Configuration();
        igniteSink.open(p);

        // the port to connect to
        final int port;
        try {
            final ParameterTool params = ParameterTool.fromArgs(args);
            port = params.getInt("port");
        } catch (Exception e) {
            System.err.println("No port specified. Please run 'SocketWindowWordCount --port <port>'");
            return;
        }

        // get the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // get input data by connecting to the socket
        DataStream<String> text = env.socketTextStream("localhost", port, "\n");

        // parse the data, group it, window it, and aggregate the counts
        SingleOutputStreamOperator<Map<String, String>> windowCounts = text
            .map(new MapFunction<String, Map<String, String>>() {
                @Override
                public Map<String, String> map(String value) throws Exception {
                    Map<String, String> myMap = new HashMap<>();
                    myMap.put(value, "test data value");
                    return myMap;
                }
            });


        // print the results with a single thread, rather than in parallel
        windowCounts.addSink(igniteSink);
        env.execute("Socket Window WordCount");
    }

}