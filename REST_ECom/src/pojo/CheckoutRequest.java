package pojo;

import java.util.List;

public class CheckoutRequest {
	
	private List<CheckoutSubRequest> orders;

	public List<CheckoutSubRequest> getOrders() {
		return orders;
	}

	public void setOrders(List<CheckoutSubRequest> orders) {
		this.orders = orders;
	}

}
 