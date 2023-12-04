package com.Prashant.WaterInventory.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Prashant.WaterInventory.dto.BaseResponse;
import com.Prashant.WaterInventory.dto.PagesRequest;
import com.Prashant.WaterInventory.service.VendorService;
import com.Prashant.WaterInventory.service.WaterService;
import com.itextpdf.text.DocumentException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class TestContoller {

	@Autowired(required = true)
	WaterService waterService;

	@Autowired(required = true)
	VendorService vendorService;

	@GetMapping("/download")
	public void pdfAsInResponse(String email) throws DocumentException, IOException {
		waterService.listOfAllDataDownload(email);

	}

	@PostMapping("/sendemail-withfilters")
	public ResponseEntity<BaseResponse> sendEmailWithFilters(@ModelAttribute PagesRequest pagesRequest)
			throws DocumentException, IOException {

		return ResponseEntity.ok(waterService.sendEmailByDate(pagesRequest.getEmail(), pagesRequest.getDate(),
				pagesRequest.getVendorId()));

	}

	@PostMapping("/add-vendor-data")
	public ResponseEntity<String> saveVendor(HttpServletRequest request) throws Exception {
		vendorService.addVendorDetail(request);
		return new ResponseEntity<>("redirect:/retrive-water-inventory", HttpStatus.OK);

	}

//	@PostMapping("/generate-pdf")
//	public ResponseEntity<InputStreamResource> downloadInventoryPdf(@ModelAttribute PagesRequest pagesRequest)
//			throws DocumentException, IOException {
//
//		byte[] inventoryPDF = waterService.generateWaterInventoryPDF(pagesRequest);
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.add("Content-Disposition", "inline; filename=upload.pdf");
//
//		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(inventoryPDF);
//
//		return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_PDF)
//				.body(new InputStreamResource(byteArrayInputStream));
//	}
	@PostMapping("/generate-pdf")
	public ResponseEntity<byte[]> downloadInventoryPdf(@ModelAttribute PagesRequest pagesRequest)
			throws DocumentException, IOException {

		byte[] inventoryPDF = waterService.generateWaterInventoryPDF(pagesRequest);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "data.pdf");

		return ResponseEntity.ok().headers(headers).body(inventoryPDF);
	}
}
