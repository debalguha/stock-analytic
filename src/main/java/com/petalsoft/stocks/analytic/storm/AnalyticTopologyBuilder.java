package com.petalsoft.stocks.analytic.storm;

import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.google.common.collect.Maps;
import com.petalsoft.stocks.analytic.storm.bolt.AggregatorBolt;
import com.petalsoft.stocks.analytic.storm.bolt.AnalyticFieldsEmitterBolt;
import com.petalsoft.stocks.analytic.storm.bolt.PriceAnalytic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.storm.jms.JmsMessageProducer;
import org.apache.storm.jms.JmsProvider;
import org.apache.storm.jms.JmsTupleProducer;
import org.apache.storm.jms.bolt.JmsBolt;
import org.apache.storm.jms.spout.JmsSpout;

import javax.jms.*;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * Created by dguha on 5/31/2016.
 */
public class AnalyticTopologyBuilder {
    public static TopologyBuilder buildStockAnalyticTopology(Properties jmsProperties) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("STOCK_SOURCE", buildJmsSpout(createJmsTupleProducer(), createIncomingJmsProvider(jmsProperties)));
        builder.setBolt("ANALYTIC_FIELDS", new AnalyticFieldsEmitterBolt()).shuffleGrouping("STOCK_SOURCE");//, TOPOLOGY_STREAMS.STOCK_SOURCE.name());
        builder.setBolt("ANALYTIC_PRODUCER", new AggregatorBolt()).fieldsGrouping("ANALYTIC_FIELDS", new Fields(TOPOLOGY_FIELDS.TICKER.name()));
        builder.setBolt("ANALYTIC_SENDER", buildJmsBolt(createJmsMessageproducer(), createOutgoingJmsProvider(jmsProperties))).shuffleGrouping("ANALYTIC_PRODUCER");
        return builder;
    }

    private static JmsBolt buildJmsBolt(JmsMessageProducer jmsMessageproducer, JmsProvider outgoingJmsProvider) {
        JmsBolt jmsBolt = new JmsBolt();
        jmsBolt.setAutoAck(true);
        jmsBolt.setJmsMessageProducer(jmsMessageproducer);
        jmsBolt.setJmsProvider(outgoingJmsProvider);
        return jmsBolt;
    }

    private static JmsMessageProducer createJmsMessageproducer() {
        return new JmsMessageProducer() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 3601576560319966965L;

			@Override
            public Message toMessage(Session session, Tuple tuple) throws JMSException {
                return session.createObjectMessage((PriceAnalytic)tuple.getValueByField(TOPOLOGY_FIELDS.PRICE_ANALYTIC.name()));
            }
        };
    }

    private static JmsProvider createIncomingJmsProvider(Properties jmsProperties) {
        return new JmsProvider() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -1010466552737945666L;

			@Override
            public ConnectionFactory connectionFactory() throws Exception {
                return new ActiveMQConnectionFactory(jmsProperties.getProperty("mq.broker.url"));
            }

            @Override
            public Destination destination() throws Exception {
                return new ActiveMQQueue(jmsProperties.getProperty("ticker.queue.name"));
            }
        };
    }

    private static JmsProvider createOutgoingJmsProvider(Properties jmsProperties) {
        return new JmsProvider() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -4902461074358441434L;

			@Override
            public ConnectionFactory connectionFactory() throws Exception {
                return new ActiveMQConnectionFactory(jmsProperties.getProperty("mq.broker.url"));
            }

            @Override
            public Destination destination() throws Exception {
                return new ActiveMQQueue(jmsProperties.getProperty("mq.out.queue.name"));
            }
        };
    }

    private static JmsTupleProducer createJmsTupleProducer() {
        return new JmsTupleProducer() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -4641476852138345400L;

			@SuppressWarnings("unchecked")
			@Override
            public Values toTuple(Message message) throws JMSException {
                MapMessage mapMessage = ((MapMessage)message);
                Map<String, Object> outputMap = Maps.newHashMap();
                Enumeration<String> mapNames = mapMessage.getMapNames();
                while(mapNames.hasMoreElements()) {
                    String key = mapNames.nextElement().toString();
                    outputMap.put(key, mapMessage.getObject(key));
                }
                return new Values(outputMap);
            }

            @Override
            public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
                //outputFieldsDeclarer.declareStream(TOPOLOGY_STREAMS.STOCK_SOURCE.name(), new Fields(TOPOLOGY_FIELDS.STOCK_MAP.name()));
            	outputFieldsDeclarer.declare(new Fields(TOPOLOGY_FIELDS.STOCK_MAP.name()));
            }
        };
    }

    private static JmsSpout buildJmsSpout(JmsTupleProducer tupleProducer, JmsProvider jmsProvider){
        JmsSpout jmsSpout = new JmsSpout();
        jmsSpout.setJmsTupleProducer(tupleProducer);
        jmsSpout.setJmsProvider(jmsProvider);
        jmsSpout.setJmsAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        return jmsSpout;
    }
}
