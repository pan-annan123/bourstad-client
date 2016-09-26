package stocks.client;

import java.io.Serializable;

public class Transaction implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean buy;
	private String ticker;
	private int amount;
	private int status;
	private transient String message;
	
	public Transaction(boolean buy, String ticker, int amount) {
		this.buy = buy;
		this.ticker = ticker;
		this.amount = amount;
		status = -1;
	}

	public boolean isBuy() {
		return buy;
	}

	public void setBuy(boolean buy) {
		this.buy = buy;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
}
