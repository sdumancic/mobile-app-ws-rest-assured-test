package com.appsdeveloperblog.app.ws.restassuredtest;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;



import io.restassured.RestAssured;
import io.restassured.response.Response;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsersWebServiceEnpointTest {

	
	private final String CONTEXT_PATH="/mobile-app-ws";
	private final String EMAIL_ADDRESS="sanjin.dumancic@yahoo.com";
	
	private static String userId;
	private static String authorizationHeader;
	private static List<Map<String, String>> addresses;
	
	@Before
	public void setUp() throws Exception {
		RestAssured.baseURI="http://localhost";
		RestAssured.port=8081;
	}

	/*
	 * testUserLogin()
	 * 
	 */
	@Test
	public final void _10_testUserLogin() {
		Map<String, String> loginDetails = new HashMap<>();
		loginDetails.put("email", EMAIL_ADDRESS);
		loginDetails.put("password", "123");
		
		Response response = given()
			.contentType("application/json")
			.accept("application/json")
			.body(loginDetails)
			.when().post(CONTEXT_PATH+"/users/login")
			.then().statusCode(200).extract().response();
		
		authorizationHeader = response.header("Authorization");
		userId = response.header("UserID");
		
		assertNotNull(authorizationHeader);
		assertNotNull(userId);
	}
	
	@Test
	public final void _20_testGetUserDetails() {
		
		Response response = given()
				.pathParam("id", userId)
				.header("Authorization",authorizationHeader)
				.accept("application/json")
				.when().get(CONTEXT_PATH+"/users/{id}")
				.then().statusCode(200)
				.contentType("application/json").extract().response();
		
		String userPublicId = response.jsonPath().getString("userId");
		String emailAddress = response.jsonPath().getString("email");
		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");
		addresses = response.jsonPath().getList("addresses");
		String addressId = addresses.get(0).get("addressId");
		
		assertNotNull(userPublicId);
		assertNotNull(emailAddress);
		assertNotNull(firstName);
		assertNotNull(lastName);
		assertNotNull(addressId);
		assertEquals(EMAIL_ADDRESS,emailAddress);
		assertTrue(addresses.size()==2);
		assertTrue(addressId.length()==30);
		
	}
	
	@Test
	public final void _30_testUpdateUserDetails() {
		
		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", "new_firstName");
		userDetails.put("lastName", "new_lastName");
		
		Response response = given()
				.contentType("application/json")
				.accept("application/json")
				.header("Authorization",authorizationHeader)
				.pathParam("id", userId)
				.body(userDetails)
				.when().put(CONTEXT_PATH+"/users/{id}")
				.then().statusCode(200)
				.contentType("application/json")
				.extract().response();
		
		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");
		
		List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");
		
		assertEquals("new_firstName",firstName);
		assertEquals("new_lastName",lastName);
		assertNotNull(storedAddresses);
		assertTrue(addresses.size()==storedAddresses.size());
		assertEquals(addresses.get(0).get("streetName"),storedAddresses.get(0).get("streetName"));
		
	}
	
	@Test
	public final void _40_testDeleteUserDetails() {
		
		Response response = given()
			.header("Authorization",authorizationHeader)
			.accept("application/json")
			.pathParam("id", userId)
			.when().delete(CONTEXT_PATH+"/users/{id}")
			.then().statusCode(200)
			.contentType("application/json")
			.extract().response();
		
		String operationResult = response.jsonPath().getString("operationResult");
		assertEquals("SUCCESS",operationResult);
	}

}
