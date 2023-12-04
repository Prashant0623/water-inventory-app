package com.Prashant.WaterInventory.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Prashant.WaterInventory.dto.PagesRequest;
import com.Prashant.WaterInventory.dto.WaterResponse;
import com.Prashant.WaterInventory.entity.VendorModel;
import com.Prashant.WaterInventory.service.VendorService;
import com.Prashant.WaterInventory.service.WaterService;
import com.itextpdf.text.DocumentException;

import jakarta.servlet.http.HttpServletRequest;

@Controller

public class WaterController {

	@Autowired
	private WaterService waterService;

	@Autowired
	VendorService vendorService;

	@GetMapping("/")
	public String getInventorydetail(ModelMap modelMap, @RequestParam(defaultValue = "0") Integer page, Long vendorId)
			throws DocumentException, IOException {

		Page<WaterResponse> inventorylist = waterService.getAllWaterInventoryPage(PageRequest.of(page, 5), vendorId);

		modelMap.addAttribute("listOfInventoryBydateByVendorname", inventorylist);

		List<VendorModel> allVendors = vendorService.getAllvendor();
		modelMap.addAttribute("allVendors", allVendors);
		modelMap.addAttribute("currentPage", page);
		return "Index";

	}

	@PostMapping("/get-with-filters")
	public String getAllInventoryPagination(@ModelAttribute PagesRequest pagesRequest, ModelMap modelMap,
			String email) {

		try {

			Long vId = pagesRequest.getVendorId();
			String vDate = pagesRequest.getDate();

			Page<WaterResponse> listOfInventoryBydateByVendorname = waterService.listOfInventoryBydateByVendorname(
					pagesRequest.getVendorId(), pagesRequest.getDate(), PageRequest.of(pagesRequest.getPage(), 5),
					email);

			modelMap.addAttribute("vendorId", vId);
			modelMap.addAttribute("vendorDate", vDate);
			modelMap.addAttribute("listOfInventoryBydateByVendorname", listOfInventoryBydateByVendorname);

			modelMap.addAttribute("currentPage", pagesRequest.getPage());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Index";

	}

	@GetMapping("/add-water-inventory")
	public String homeget(ModelMap modelMap) {

		List<VendorModel> allvendor = vendorService.getAllvendor();
		modelMap.addAttribute("allVendor", allvendor);

		return "InventoryAdd";
	}

	@PostMapping("/add-water-inventory")
	public ResponseEntity<String> addWaterInventoryDetail(HttpServletRequest request) {

		waterService.saveWaterInventoryData(request);
		return new ResponseEntity<>("redirect:/retrive-water-inventory", HttpStatus.OK);

	}

	@GetMapping("/sendemail-withfilters")
	public String sendemails() {
		return "Index";
	}

}
