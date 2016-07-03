/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

/**
 * Client that uses the {@link ProducerTemplate} to easily exchange messages with the Server.
 * <p/>
 * Requires that the JMS broker is running, as well as CamelServer
 */
public final class CamelMongoJmsStockClient {
    private static Properties systemProps;
    @SuppressWarnings("resource")
	public static void main(final String[] args) throws Exception {
        systemProps = loadProperties();
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("camel-client.xml");
        ProducerTemplate camelTemplate = context.getBean("camelTemplate", ProducerTemplate.class);
        List<Map<String, Object>> stocks = readJsonsFromMongoDB();
        for(Map<String, Object> stock : stocks) {
            stock.remove("_id");stock.remove("Earnings Date");
            camelTemplate.sendBody(String.format("jms:queue:%s", systemProps.getProperty("ticker.queue.name")), ExchangePattern.InOnly, stock);
        }
        new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				Map<String, Object> aRandomStock = stocks.get(RandomUtils.nextInt(0, stocks.size()));
				aRandomStock.put("Price", ((Double)aRandomStock.get("Price"))+RandomUtils.nextFloat(0.1f, 9.99f));
				camelTemplate.sendBody(String.format("jms:queue:%s", systemProps.getProperty("ticker.queue.name")), ExchangePattern.InOnly, aRandomStock);
			}
		}, 1000, 2000);
    }

    private static Properties loadProperties() throws Exception{
        Properties props = new Properties();
        props.load(CamelMongoJmsStockClient.class.getClassLoader().getResourceAsStream("application.properties"));
        return props;
    }

    @SuppressWarnings("unchecked")
	public static List<Map<String, Object>> readJsonsFromMongoDB() throws Exception{
        DBCursor dbObjects = new MongoClient("localhost").getDB("local").getCollection("stocks").find();
        List<Map<String, Object>> outputList = new ArrayList<>();
        while(dbObjects.hasNext())
            outputList.add(dbObjects.next().toMap());
        return outputList;
    }
}
