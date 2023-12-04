package com.Prashant.WaterInventory.dto;

import lombok.Data;

@Data
public class PagesRequest {

	private String email;
	private String date;
	private Long vendorId;
	private Integer page;

}
