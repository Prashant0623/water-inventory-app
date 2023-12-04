package com.Prashant.WaterInventory.service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Prashant.WaterInventory.config.EmailConfigure;
import com.Prashant.WaterInventory.dto.BaseResponse;
import com.Prashant.WaterInventory.dto.PagesRequest;
import com.Prashant.WaterInventory.dto.WaterResponse;
import com.Prashant.WaterInventory.entity.VendorModel;
import com.Prashant.WaterInventory.entity.WaterModel;
import com.Prashant.WaterInventory.repository.WaterRepo;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class WaterService {

	@Autowired
	private WaterRepo waterRepo;
	@Autowired
	private EmailConfigure emailConfigure;

	// save water inventory detail to Database
	public String saveWaterInventoryData(HttpServletRequest servletRequest) {

		Long vendorId = Long.parseLong(servletRequest.getParameter("vendorname"));

		int quantity = Integer.parseInt(servletRequest.getParameter("quantity"));
		double price = Double.parseDouble(servletRequest.getParameter("price"));

		WaterModel waterModel = new WaterModel();

		VendorModel vendorModel = new VendorModel();
		vendorModel.setVendorId(vendorId);

		waterModel.setVendorModel(vendorModel);
		waterModel.setDate(servletRequest.getParameter("date"));
		waterModel.setQuantity(quantity);
		waterModel.setPrice(price);
		double totalPrice = quantity * price;
		waterModel.setTotalPrice(totalPrice);
		waterRepo.save(waterModel);

		return "InventoryAdd";

	}

	private void addTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell();

		cell.setPhrase(new Paragraph("VendorName"));
		table.addCell(cell);

		cell.setPhrase(new Paragraph("Date"));
		table.addCell(cell);

		cell.setPhrase(new Paragraph("Quantity"));
		table.addCell(cell);

		cell.setPhrase(new Paragraph("Price"));
		table.addCell(cell);

		cell.setPhrase(new Paragraph("TotalPrice"));
		table.addCell(cell);

	}

	// pdf-invoice-set
	public String getInvoiceOfdateFilter(List<WaterResponse> responseDate, String email)
			throws DocumentException, IOException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document, outputStream);

		document.open();

		document.add(new Paragraph("Inventory-detail"));

		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);

		addTableHeader(table);

		Double grandTotal = responseDate.stream().mapToDouble(o -> o.getTotalPrice()).sum();

		responseDate.stream().forEach(list -> {
			table.addCell(list.getVendorname());
			table.addCell(list.getDate());
			table.addCell(String.valueOf(list.getQuantity()));
			table.addCell(String.valueOf(list.getPrice()));
			table.addCell(String.valueOf(list.getTotalPrice()));

		});

		document.add(table);
		document.add(new Paragraph("Grand Total : " + grandTotal + " INR"));

		document.close();

		@SuppressWarnings("resource")
		FileOutputStream fileOut = new FileOutputStream("src/main/resources/Inventory.pdf");

		fileOut.write(outputStream.toByteArray());

		try {
			if (email != null) {
				emailConfigure.sendEmail(email, "Water-Inventory-detail");

			}

		} catch (Exception e) {
			System.out.println("Inside catch");
			e.printStackTrace();
		}

		return "pdf has been created !!";
	}

	// method to get response as in pdf
	public void listOfAllDataDownload(String email) throws DocumentException, IOException {

		List<WaterResponse> allres = getAllWaterInventory(email);

		getInvoiceOfdateFilter(allres, email);

	}

	// this method does to keep vendorname on list
	public String getVendorNameForWaterModel(WaterModel waterModel) {
		if (waterModel.getVendorModel() != null) {
			return waterModel.getVendorModel().getVendorname();
		} else {
			return "unkhown vendor";
		}
	}

//	 get waterinventorylist
	public List<WaterResponse> getAllWaterInventory(String email) throws DocumentException, IOException {

		List<WaterResponse> list = new ArrayList<>();

		List<WaterModel> inventoryPage = waterRepo.findAll();
		inventoryPage.stream().forEach(r -> {
			WaterResponse response = new WaterResponse();
			response.setDate(r.getDate());
			response.setQuantity(r.getQuantity());
			response.setPrice(r.getPrice());
			response.setTotalPrice(r.getTotalPrice());

			String vendorName = getVendorNameForWaterModel(r);
			response.setVendorname(vendorName);
			list.add(response);

		});
		getInvoiceOfdateFilter(list, email);

		return list;
	}

	public Page<WaterResponse> getAllWaterInventoryPage(Pageable pageable, Long vendorId)
			throws DocumentException, IOException {

		Page<WaterModel> inventoryPage = waterRepo.findAllOrderedByIdDesc(pageable);

		return inventoryPage.map(waterModel -> {
			WaterResponse waterResponse = new WaterResponse();

			waterResponse.setDate(waterModel.getDate());
			waterResponse.setPrice(waterModel.getPrice());
			waterResponse.setQuantity(waterModel.getQuantity());
			waterResponse.setTotalPrice(waterModel.getTotalPrice());

			// Set vendor name in the response
			String vendorName = getVendorNameForWaterModel(waterModel);
			waterResponse.setVendorname(vendorName);

			return waterResponse;
		});
	}

	// this method goes to get data by DATE attribute
	public List<WaterModel> sendAllinventoryByDate(String date) {

		List<WaterModel> listofWaterbyDate = new ArrayList<>();

		if (date != null) {
			List<WaterModel> findByDate = waterRepo.findByDate(date);

			return findByDate;
		}
		return listofWaterbyDate;

	}

	// response for date
	public List<WaterResponse> listofInventoryBydate(String date, String email) throws DocumentException, IOException {
		List<WaterResponse> listWaterResponseBydate = new ArrayList<>();

		List<WaterModel> listOffindByDate = waterRepo.findByDate(date);
		listOffindByDate.stream().forEach(list -> {
			WaterResponse waterResponse = new WaterResponse();
			waterResponse.setVendorname(list.getVendorModel().getVendorname());
			waterResponse.setDate(list.getDate());
			waterResponse.setPrice(list.getPrice());
			waterResponse.setQuantity(list.getQuantity());
			waterResponse.setTotalPrice(list.getTotalPrice());
			listWaterResponseBydate.add(waterResponse);

		});

		getInvoiceOfdateFilter(listWaterResponseBydate, email);
		return listWaterResponseBydate;

	}

	public List<WaterModel> getallInventoryByVendorname(Long vendorId) {

		List<WaterModel> listofWaterbyVendor = new ArrayList<>();

		if (vendorId != null) {

			List<WaterModel> findByVendorId = waterRepo.findByVendorId(vendorId);
			return findByVendorId;
		}
		return listofWaterbyVendor;
	}

	// set response for vendor detail
	public List<WaterResponse> getallInventoryListByVendor(Long vendorId, String email)
			throws DocumentException, IOException {

		List<WaterResponse> listWaterResponse = new ArrayList<>();

		List<WaterModel> listOfVendor = waterRepo.findByVendorId(vendorId);
		listOfVendor.stream().forEach(t -> {
			WaterResponse waterResponse = new WaterResponse();

			waterResponse.setVendorname(t.getVendorModel().getVendorname());
			waterResponse.setVendorId(t.getVendorModel().getVendorId());
			waterResponse.setDate(t.getDate());
			waterResponse.setPrice(t.getPrice());
			waterResponse.setQuantity(t.getQuantity());
			waterResponse.setTotalPrice(t.getTotalPrice());
			listWaterResponse.add(waterResponse);

		});
		getInvoiceOfdateFilter(listWaterResponse, email);

		return listWaterResponse;
	}

	// response for both vendor and data
	public List<WaterResponse> listofFilterInventoryBydateAndByVendor(String date, Long vendorId, String email)
			throws DocumentException, IOException {
		List<WaterResponse> listFilterWaterResponse = new ArrayList<>();
		List<WaterModel> findByDateAndVendorId = waterRepo.findByDateAndVendorId(date, vendorId);
		findByDateAndVendorId.stream().forEach(list -> {
			WaterResponse waterResponse = new WaterResponse();
			waterResponse.setVendorname(list.getVendorModel().getVendorname());
			waterResponse.setDate(list.getDate());
			waterResponse.setPrice(list.getPrice());
			waterResponse.setQuantity(list.getQuantity());
			waterResponse.setTotalPrice(list.getTotalPrice());
			listFilterWaterResponse.add(waterResponse);

		});

		getInvoiceOfdateFilter(listFilterWaterResponse, email);
		return listFilterWaterResponse;

	}

	@SuppressWarnings("unlikely-arg-type")
	public Page<WaterResponse> listOfInventoryBydateByVendorname(Long vendorId, String date, Pageable pageable,
			String email) throws DocumentException, IOException {
		List<WaterResponse> response = new ArrayList<>();

		if (pageable != null) {
			response = getAllWaterInventory(email);
		}

		if (date != null && !date.equals("") && vendorId != null && !vendorId.equals("") && vendorId != 0) {
			response = listofFilterInventoryBydateAndByVendor(date, vendorId, email);
		} else if (date != null && !date.equals("")) {
			response = listofInventoryBydate(date, email);
		} else if (vendorId != null && !vendorId.equals("")) {
			response = getallInventoryListByVendor(vendorId, email);
		}

		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;

		List<WaterResponse> listOfInventory;

		if (response.size() < startItem) {
			listOfInventory = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, response.size());
			listOfInventory = response.subList(startItem, toIndex);
		}

		Page<WaterResponse> inventoryPage = new PageImpl<>(listOfInventory, pageable, response.size());

		return inventoryPage;

	}

	// email operations
	@SuppressWarnings("unlikely-arg-type")
	public BaseResponse sendEmailByDate(String email, String date, Long vendorId)
			throws DocumentException, IOException {
		BaseResponse response = new BaseResponse();

		if (!email.isEmpty()) {
			if (date != null && !date.equals("") && vendorId != null && !vendorId.equals("") && vendorId != 0)

			{
				listofFilterInventoryBydateAndByVendor(date, vendorId, email);
			} else if (date != null && !date.equals("")) {
				listofInventoryBydate(date, email);
				response.setMessage("email sent successfull");

			} else if (vendorId != null && !vendorId.equals("")) {
				getallInventoryListByVendor(vendorId, email);
				response.setMessage("email sent successfull");
			} else {
				getAllWaterInventory(email);
				response.setMessage("email sent successfull");

			}

		}
		return response;

	}

	@SuppressWarnings("unlikely-arg-type")
	public byte[] generateWaterInventoryPDF(PagesRequest pagesRequest) throws DocumentException, IOException {
		List<WaterResponse> convertTolistofInventory = new ArrayList<>();

		if (pagesRequest.getDate() != null && !pagesRequest.getDate().equals("") && pagesRequest.getVendorId() != null
				&& !pagesRequest.getVendorId().equals("") && pagesRequest.getVendorId() != 0) {
			convertTolistofInventory = listofFilterInventoryBydateAndByVendor(pagesRequest.getDate(),
					pagesRequest.getVendorId(), pagesRequest.getEmail());
		} else if (pagesRequest.getDate() != null && !pagesRequest.getDate().equals("")) {
			convertTolistofInventory = listofInventoryBydate(pagesRequest.getDate(), pagesRequest.getEmail());
		} else if (pagesRequest.getVendorId() != null && !pagesRequest.getVendorId().equals("")) {
			convertTolistofInventory = getallInventoryListByVendor(pagesRequest.getVendorId(), pagesRequest.getEmail());
		} else if (pagesRequest.getDate() == null && pagesRequest.getVendorId() == null) {
			getAllWaterInventory(pagesRequest.getEmail());
		}
		return downloadWaterInventoryPDF(convertTolistofInventory);
	}

	// download pdf
	public byte[] downloadWaterInventoryPDF(List<WaterResponse> listwaterResponses)
			throws DocumentException, IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document, outputStream);

		document.open();
		document.add(new Paragraph("invoice-inventory"));

		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);

		addTableHeader(table);

		Double grandTotal = listwaterResponses.stream().mapToDouble(o -> o.getTotalPrice()).sum();

		listwaterResponses.stream().forEach(inventorydetail -> {
			table.addCell(inventorydetail.getVendorname());
			table.addCell(inventorydetail.getDate());
			table.addCell(String.valueOf(inventorydetail.getQuantity()));
			table.addCell(String.valueOf(inventorydetail.getPrice()));
			table.addCell(String.valueOf(inventorydetail.getTotalPrice()));

		});
		document.add(table);
		document.add(new Paragraph("Grand Total : " + grandTotal + " INR"));
		document.close();

//		@SuppressWarnings("resource")
//		FileOutputStream fileOutput = new FileOutputStream("src/main/resources/Inventory-download.pdf");

//		fileOutput.write(outputStream.toByteArray());
		return outputStream.toByteArray();

	}

}
