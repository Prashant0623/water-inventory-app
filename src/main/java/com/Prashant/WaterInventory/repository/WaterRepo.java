package com.Prashant.WaterInventory.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.Prashant.WaterInventory.entity.WaterModel;

@Repository
public interface WaterRepo extends JpaRepository<WaterModel, Long> {

	@Query(value = "SELECT* FROM water_model where vendor_id=:id", nativeQuery = true)
	public List<WaterModel> findByVendorId(Long id);

	@Query(value = "SELECT* FROM water_model where date=:date", nativeQuery = true)
	public List<WaterModel> findByDate(String date);

	@Query(value = "SELECT* FROM water_model where date=:date AND vendor_id=:id", nativeQuery = true)
	public List<WaterModel> findByDateAndVendorId(String date, Long id);

	@Query(value = "SELECT * FROM water_model f order by f.id desc ", nativeQuery = true)
	public Page<WaterModel> findAllOrderedByIdDesc(Pageable pageable);

}
