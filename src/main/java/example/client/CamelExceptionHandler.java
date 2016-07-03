package example.client;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("exceptionHandler")
public class CamelExceptionHandler implements Processor{

	@Override
	public void process(Exchange exchange) throws Exception {
		exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class).printStackTrace();
	}

}
