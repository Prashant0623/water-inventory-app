package com.Prashant.WaterInventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Prashant.WaterInventory.dto.VendorResponse;
import com.Prashant.WaterInventory.service.VendorService;
import com.Prashant.WaterInventory.service.WaterService;

@Controller
public class VendorController {

	@Autowired
	private VendorService vendorService;
	@Autowired
	WaterService waterService;

	@GetMapping("/add-vendor-data")
	public String vendorPage(ModelMap modelMap, @RequestParam(defaultValue = "0") Integer page, Pageable pageable) {
		System.err.println("vendor-page");
		Page<VendorResponse> vendorsList = vendorService.getAllVendorsList(PageRequest.of(page, 2));
		modelMap.addAttribute("vendorsList", vendorsList);
		modelMap.addAttribute("currentPage", page);
		return "add-vendor";
	}

}
