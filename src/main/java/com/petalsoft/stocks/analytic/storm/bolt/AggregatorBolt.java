package com.petalsoft.stocks.analytic.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.google.common.collect.Maps;
import com.petalsoft.stocks.analytic.storm.TOPOLOGY_FIELDS;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Map;

/**
 * Created by dguha on 6/5/2016.
 */
@SuppressWarnings("serial")
public class AggregatorBolt extends AbstractRichBolt {
    private Map<String, Double> lastTickerPriceMap;

    @SuppressWarnings("rawtypes")
	@Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
        lastTickerPriceMap = Maps.newConcurrentMap();
    }

    @Override
    public void execute(Tuple input) {
        String ticker = input.getStringByField(TOPOLOGY_FIELDS.TICKER.name());
        Double price = input.getDoubleByField(TOPOLOGY_FIELDS.TICKER_PRICE.name());
        if(!lastTickerPriceMap.containsKey(ticker))
        	lastTickerPriceMap.put(ticker, price);
        Double lastTickerPrice = lastTickerPriceMap.get(ticker);
        Double percentChange = calculatePercentChange(price, lastTickerPrice);
        lastTickerPriceMap.put(ticker, price);
        outputCollector.emit(new Values(new PriceAnalytic(ticker, new DateTime().toString(DateTimeFormat.forPattern("MM/dd/YYYY HH:mmss")), price, percentChange, calculateChangeDirection(price, lastTickerPrice))));
    }

    private ChangeDirection calculateChangeDirection(Double price, Double lastTickerPrice) {
		return price.compareTo(lastTickerPrice) == 0? ChangeDirection.NONE: price.compareTo(lastTickerPrice) > 0? ChangeDirection.UP: ChangeDirection.DOWN;
	}

	private Double calculatePercentChange(Double price, Double initialPrice) {
        return ((price-initialPrice)/initialPrice)*100;
    }

    @Override
    public void cleanup() {}

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(TOPOLOGY_FIELDS.PRICE_ANALYTIC.name()));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {return null;}
}
