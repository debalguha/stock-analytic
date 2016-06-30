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

import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.util.IOHelper;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Client that uses the {@link ProducerTemplate} to easily exchange messages with the Server.
 * <p/>
 * Requires that the JMS broker is running, as well as CamelServer
 */
public final class CamelMongoJmsStockClient {
    private static Properties systemProps;
    public static void main(final String[] args) throws Exception {
        systemProps = loadProperties();
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("camel-client.xml");
        ProducerTemplate camelTemplate = context.getBean("camelTemplate", ProducerTemplate.class);
        for(Map<String, Object> stock : readJsonsFromMongoDB()) {
            stock.remove("_id");stock.remove("Earnings Date");
            camelTemplate.sendBody(String.format("jms:queue:%s", systemProps.getProperty("ticker.queue.name")), ExchangePattern.InOnly, stock);
        }
        IOHelper.close(context);
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
