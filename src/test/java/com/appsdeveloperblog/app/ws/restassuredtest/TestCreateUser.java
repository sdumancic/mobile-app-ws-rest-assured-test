package com.appsdeveloperblog.app.ws.restassuredtest;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;


import io.restassured.RestAssured;
import io.restassured.response.Response;

public class TestCreateUser {

	private final String CONTEXT_PATH="/mobile-app-ws";
	
	@Before
	public void setUp() throws Exception {
		RestAssured.baseURI="http://localhost";
		RestAssured.port=8081;
	}

	@Test
	public void testCreateUser() {
		
		List<Map<String, Object>> userAddresses = new ArrayList<>();
		
		Map<String, Object> shippingAddress = new HashMap<>();
		shippingAddress.put("city", "Vancouver");
		shippingAddress.put("country", "Canada");
		shippingAddress.put("streetName", "123 Street Name");
		shippingAddress.put("postalCode", "ABCCBA");
		shippingAddress.put("type", "Shipping");
		
		Map<String, Object> billingAddress = new HashMap<>();
		billingAddress.put("city", "Vancouver");
		billingAddress.put("country", "Canada");
		billingAddress.put("streetName", "123 Street Name");
		billingAddress.put("postalCode", "ABCCBA");
		billingAddress.put("type", "Billing");
		
		userAddresses.add(shippingAddress);
		userAddresses.add(billingAddress);
		
		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", "Sanjin");
		userDetails.put("lastName", "Dumančić");
		userDetails.put("email", "sanjin.dumancic@yahoo.com");
		userDetails.put("password","123");
		userDetails.put("addresses",userAddresses);
		
		
		Response response = given()
			.contentType("application/json")
			.accept("application/json")
			.body(userDetails)
			.when()
			.post(CONTEXT_PATH + "/users")
			.then()
			.statusCode(200)
			.contentType("application/json")
			.extract()
			.response();
		
		String userId = response.jsonPath().getString("userId");
		assertNotNull(userId);
		assertTrue(userId.length()==30);
		
		String bodyString = response.body().asString();
		System.out.println(bodyString);
		try {
			JSONObject responseBodyJson = new JSONObject(bodyString);
			System.out.println(responseBodyJson.toString());
			JSONArray addresses = responseBodyJson.getJSONArray("addresses");
			assertNotNull(addresses);
			assertTrue(addresses.length()==2);
			
			String addressId = (String) addresses.getJSONObject(0).get("addressId");
			assertNotNull(addressId);
			assertTrue(addressId.length()==30);
		} catch (JSONException e) {
			fail(e.getMessage());
		}
	}

}
