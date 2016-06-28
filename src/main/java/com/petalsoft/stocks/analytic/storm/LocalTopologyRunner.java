package com.petalsoft.stocks.analytic.storm;

import java.util.Properties;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

/**
 * Created by dguha on 6/6/2016.
 */
public class LocalTopologyRunner {
    public static void main(String args[]) throws Exception {
        TopologyBuilder topologyBuilder = AnalyticTopologyBuilder.buildStockAnalyticTopology(loadProperties());
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("LOCAL_STOCK_TOPOLOGY", new Config(), topologyBuilder.createTopology());
        /*Utils.sleep(40000);
        cluster.killTopology("LOCAL_STOCK_TOPOLOGY");
        cluster.shutdown();*/
    }
    private static Properties loadProperties() throws Exception{
        Properties props = new Properties();
        props.load(LocalTopologyRunner.class.getClassLoader().getResourceAsStream("application.properties"));
        return props;
    }
}
