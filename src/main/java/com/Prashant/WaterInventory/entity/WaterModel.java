package com.Prashant.WaterInventory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class WaterModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String date;
	private int quantity;
	private double price;
	private double totalPrice;

	@ManyToOne
	@JoinColumn(name = "vendor_id")
	private VendorModel vendorModel;
	

}
