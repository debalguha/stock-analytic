package example.client;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

@Component("toMap")
public class ToMapConverter implements Processor{
	private DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
	@Override
	public void process(Exchange exchange) throws Exception {
		String []body = exchange.getIn().getBody(String.class).split(",");
		Map<String, Object> dataMap = Maps.newHashMap();
		dataMap.put("date", format.parseDateTime(body[0]).toDate());
		dataMap.put("value", Double.parseDouble(body[1]));
		exchange.getIn().setBody(dataMap);
	}

}
