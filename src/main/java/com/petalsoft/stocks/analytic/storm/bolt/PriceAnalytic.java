package com.petalsoft.stocks.analytic.storm.bolt;

import java.io.Serializable;

/**
 * Created by dguha on 6/5/2016.
 */
@SuppressWarnings("serial")
public class PriceAnalytic implements Serializable{
    private String ticker;
    private String timeStamp;
    private Double price;
    private Double percentChangeInPrice;
    private ChangeDirection changeDirection;

    public PriceAnalytic(String ticker, String timeStamp, Double price, Double percentChangeInPrice, ChangeDirection direction) {
        this.ticker = ticker;
        this.timeStamp = timeStamp;
        this.price = price;
        this.percentChangeInPrice = percentChangeInPrice;
        this.changeDirection = direction;
    }

    @Override
    public String toString() {
        return "PriceAnalytic{" +
                "ticker='" + ticker + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", price=" + price +
                ", percentChangeInPrice=" + percentChangeInPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriceAnalytic that = (PriceAnalytic) o;

        return ticker.equals(that.ticker);

    }

    @Override
    public int hashCode() {
        return ticker.hashCode();
    }

    public Double getPercentChangeInPrice() {

        return percentChangeInPrice;
    }

    public void setPercentChangeInPrice(Double percentChangeInPrice) {
        this.percentChangeInPrice = percentChangeInPrice;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Double getPrice() {

        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

	public ChangeDirection getChangeDirection() {
		return changeDirection;
	}

	public void setChangeDirection(ChangeDirection changeDirection) {
		this.changeDirection = changeDirection;
	}
}
