package com.samaitra;

import java.util.Collection;
import org.apache.ignite.Ignition;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.configuration.ClientConfiguration;

public class IgniteClientExample {

    public static void main(String[] args) {
        ClientConfiguration clientConfiguration = new ClientConfiguration().setAddresses("127.0.0.1:10800");
        Ignition.setClientMode(true);
        org.apache.ignite.client.IgniteClient igniteClient = Ignition.startClient(clientConfiguration);
        Collection<String> caches = igniteClient.cacheNames();

        for (String cache : caches) {
            System.out.println("cache = " +cache);
        }

        ClientCache<String, String> cache = igniteClient.getOrCreateCache("testCache");
        System.out.println("cache size = " +cache.size());
    }
}
