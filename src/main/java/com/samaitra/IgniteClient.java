package com.samaitra;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;

public class IgniteClient {

    public static void main(String[] args) {
        Ignite ignite = Ignition.start("/Users/saikat/git/streamers/src/main/resources/example-ignite-client.xml");
        IgniteCache<String, String> cache = ignite.getOrCreateCache("testCache");
        Integer i = cache.size();
        System.out.println("Cache size is "+ i);
    }
}
