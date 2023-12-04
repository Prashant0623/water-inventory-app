package com.Prashant.WaterInventory.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Prashant.WaterInventory.dto.VendorResponse;
import com.Prashant.WaterInventory.entity.VendorModel;
import com.Prashant.WaterInventory.repository.VendorRepo;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class VendorService {

	@Autowired
	private VendorRepo vendorRepo;

	public String addVendorDetail(HttpServletRequest request) throws Exception {
		VendorModel vendorModel = new VendorModel();
		vendorModel.setVendorname(request.getParameter("vendorname"));
		vendorModel.setMobileNo(request.getParameter("mobileNo"));

		if (isMobileNumberDuplicate(vendorModel.getMobileNo())) {
			throw new Exception("mobile number already exist");
//			return new ResponseEntity<>("Mobile number already exists", HttpStatus.CONFLICT);
		}
		vendorRepo.save(vendorModel);
		if (isMobileNumberDuplicate(vendorModel.getMobileNo())) {
//			return new ResponseEntity<>("Vendor added successfully", HttpStatus.OK);
		} else {
//			return new ResponseEntity<>("Failed to add vendor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return null;
	}

	private boolean isMobileNumberDuplicate(String mobileNumber) {
		VendorModel existingVendor = vendorRepo.findByMobileNo(mobileNumber);
		if (existingVendor == null) {
			return false;
		}
		if (existingVendor.getMobileNo().equals(mobileNumber)) {
			System.out.println("true");
			return true;

		} else {
			System.out.println("false");
			return false;
		}

	}

	public List<VendorModel> getAllvendor() {
		List<VendorModel> listOfVendor = vendorRepo.findAll();
		return listOfVendor;

	}

	public Page<VendorResponse> getAllVendorsList(Pageable pageable) {
		Page<VendorModel> listOfVendor = vendorRepo.findAll(pageable);

		List<VendorResponse> listOfVendorResponse = new ArrayList<>();

		listOfVendor.getContent().forEach(vendor -> {
			VendorResponse vendorResponse = new VendorResponse();
			vendorResponse.setMobileNo(vendor.getMobileNo());
			vendorResponse.setVendorname(vendor.getVendorname());
			listOfVendorResponse.add(vendorResponse);
		});

		return new PageImpl<>(listOfVendorResponse, pageable, listOfVendor.getTotalElements());
	}

}
