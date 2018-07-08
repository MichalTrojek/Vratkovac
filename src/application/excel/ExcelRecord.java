package application.excel;

public class ExcelRecord {
	private String ean, amount, price;
	private double totalPrice;

	public ExcelRecord(String ean, String amount) {
		this.ean = ean;
		this.amount = amount;
	}

	public ExcelRecord(String ean, String amount, String price) {
		this.ean = ean;
		this.amount = amount;
		this.price = price;
	}

	public ExcelRecord(String ean, String amount, String price, double totalPrice) {
		this.ean = ean;
		this.amount = amount;
		this.price = price;
		this.totalPrice = totalPrice;
	}

	public String getEan() {
		return this.ean;
	}

	public String getAmount() {
		return this.amount;
	}

	public String getPrice() {
		return this.price;
	}

	public double getTotalPrice() {
		return this.totalPrice;
	}

}
