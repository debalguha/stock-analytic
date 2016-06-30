package example.client;

import org.apache.camel.CamelContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CamelFileMongoFXClient {
    //private static Properties systemProps;
    @SuppressWarnings("resource")
	public static void main(final String[] args) throws Exception {
        //systemProps = loadProperties();
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("camel-client.xml");
        CamelContext ctx = context.getBean(CamelContext.class);
        ctx.start();
        ctx.startRoute("input");
        //IOHelper.close(context);
    }

    /*private static Properties loadProperties() throws Exception{
        Properties props = new Properties();
        props.load(CamelFileMongoFXClient.class.getClassLoader().getResourceAsStream("application.properties"));
        return props;
    }*/

}
