package com.vttp.ssf.assesment;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vttp.ssf.assesment.Model.Quotation;
import com.vttp.ssf.assesment.Service.QuotationService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
class AssesmentApplicationTests {
	@Autowired 
	public QuotationService quotationService;

	@Test
	void contextLoads() {
	}


	@Test
	void GetQuotations() throws IOException{
		

		String message= "%s";
		try{
		List<String> testList = new ArrayList<String>();
		testList.add("durian");
		testList.add("plum");
		testList.add("pear");
		Optional<Quotation> optional = quotationService.getQuotations(testList);
		}catch(HttpClientErrorException e){
			message = e.getMessage();
		}
		String status ="400";
		
		Assertions.assertTrue(message.contains(status));
	}
	

}