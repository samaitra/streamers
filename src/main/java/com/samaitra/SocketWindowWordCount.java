package com.samaitra;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.apache.ignite.sink.flink.IgniteSink;

public class SocketWindowWordCount {
    public static void main(String[] args) throws Exception {
        /** Ignite test configuration file. */
        final String GRID_CONF_FILE = "PATH_TO_PROJECT/streamers/src/main/resources/example-ignite.xml";

        IgniteSink igniteSink = new IgniteSink("testCache", GRID_CONF_FILE);

        igniteSink.setAllowOverwrite(true);

        igniteSink.setAutoFlushFrequency(5L);

        Configuration p = new Configuration();
        igniteSink.open(p);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        properties.setProperty("group.id", "test");

        // get the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // get input data by connecting to kafka
        DataStream<String> text = env
            .addSource(new FlinkKafkaConsumer010<>("mytopic", new SimpleStringSchema(), properties));

        // parse the data, group it, window it, and aggregate the counts
        SingleOutputStreamOperator<Map<String, Integer>> windowCounts = text
            .flatMap(new Splitter())
            .keyBy(0)
            .timeWindow(Time.seconds(5))
            .sum(1)
            .map(new Mapper());
        // print the results with a single thread, rather than in parallel
        // windowCounts.print();
        windowCounts.addSink(igniteSink);
        env.execute("Window WordCount");
    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word: sentence.split(" ")) {
                out.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }

    public static class Mapper implements MapFunction<Tuple2<String, Integer>, Map<String, Integer>> {
        @Override
        public Map<String, Integer> map(Tuple2<String, Integer> tuple2) throws Exception {
            Map<String, Integer> myWordMap = new HashMap<>();
            myWordMap.put(tuple2.f0, tuple2.f1);
            return myWordMap;
        }
    }
}