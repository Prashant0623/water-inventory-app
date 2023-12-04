package com.Prashant.WaterInventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Prashant.WaterInventory.entity.VendorModel;

@Repository
public interface VendorRepo extends JpaRepository<VendorModel, Long> {

	VendorModel findByMobileNo(String mobileNumber);

}
