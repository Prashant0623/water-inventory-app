package com.Prashant.WaterInventory.config;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailConfigure {

	@Autowired
	JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	public String username;

	public String sendEmail(String to, String subject) throws IOException {

		try {

			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			FileSystemResource resource = new FileSystemResource(new File(
					"/Users/prashantmehta/Documents/workspace-spring-tool-suite-4-4.15.1.RELEASE/WaterInventory/src/main/resources/inventory.pdf"));

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setFrom(username);
			helper.setText("hello prashant , your Inventory invoice here");
			helper.addAttachment(resource.getFilename(), resource, "application/pdf");

			javaMailSender.send(message);

			System.out.println("Email send successfull !!! : ");
			return "Email send successfull !!!";

		} catch (Exception e) {
			System.out.println("Exception :: Error While sending email !!! : ");
			e.printStackTrace();
		}
		return "error while sending email";

	}

}
