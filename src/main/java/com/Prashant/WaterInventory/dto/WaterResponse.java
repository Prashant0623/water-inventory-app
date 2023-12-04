package com.Prashant.WaterInventory.dto;

import lombok.Data;

@Data
public class WaterResponse {

	private Long vendorId; 
	private String vendorname;
	private String date;
	private int quantity;
	private double price;
	private double totalPrice;

}
