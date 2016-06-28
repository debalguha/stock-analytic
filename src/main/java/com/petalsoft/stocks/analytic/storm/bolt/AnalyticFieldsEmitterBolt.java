package com.petalsoft.stocks.analytic.storm.bolt;

import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.petalsoft.stocks.analytic.storm.TOPOLOGY_FIELDS;

import java.util.Map;

/**
 * Created by dguha on 6/3/2016.
 */
@SuppressWarnings("serial")
public class AnalyticFieldsEmitterBolt extends AbstractRichBolt{

    @SuppressWarnings("unchecked")
	@Override
    public void execute(Tuple input) {
        Map<String, Object> stock = (Map<String, Object>) input.getValueByField(TOPOLOGY_FIELDS.STOCK_MAP.name());
        String ticker = String.valueOf(stock.get("Ticker"));
        if(!stock.containsKey("Price"))
        	return;
        Double price = Double.valueOf(stock.get("Price").toString());
        outputCollector.emit(new Values(ticker, price, stock));
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(TOPOLOGY_FIELDS.TICKER.name(), TOPOLOGY_FIELDS.TICKER_PRICE.name(), TOPOLOGY_FIELDS.STOCK_MAP.name()));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
