package com.petalsoft.stocks.analytic.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;

import java.util.Map;

/**
 * Created by dguha on 6/3/2016.
 */
@SuppressWarnings("serial")
public abstract class AbstractRichBolt implements IRichBolt {
    protected OutputCollector outputCollector;

    @SuppressWarnings("rawtypes")
	@Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.outputCollector = collector;
    }

}
