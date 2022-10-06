package ecom;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import pojo.CheckoutRequest;
import pojo.CheckoutSubRequest;
import pojo.LoginRequest;
import pojo.LoginResponse;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ECommerceAPITest {

	public static void main(String[] args) {
		
/*-------------------------------------------------------------------Login---------------------------------------------------------------------*/
		//Request common entities
		RequestSpecification request = new RequestSpecBuilder()
		.setBaseUri("https://rahulshettyacademy.com/")
		.setContentType(ContentType.JSON)
		.build();
		
		//create object of login credentials and set their values
		LoginRequest login_request = new LoginRequest();
		login_request.setUserEmail("khitish10@gmail.com");
		login_request.setUserPassword("Asdf1234");
		
		//request call->>break given, when and then into parts
		RequestSpecification request_login = given().spec(request).body(login_request);
		
		//create object of Login Response to get token and other response parameter 
		LoginResponse login_response = request_login.when().post("api/ecom/auth/login")
													.then()
													.extract().response().as(LoginResponse.class);
		
		
		System.out.println(login_response.getToken());
		System.out.println(login_response.getUserId());
		
		//Store token into a string
		String token = login_response.getToken();
		String userid = login_response.getUserId();
/*-------------------------------------------------------------------Create Product------------------------------------------------------------------*/
		
		RequestSpecification create_request_base_spec =  new RequestSpecBuilder()
		.setBaseUri("https://rahulshettyacademy.com/")
		.addHeader("Authorization", token)
		.build();
		
		//given
		RequestSpecification create_product_main_request = given().spec(create_request_base_spec)
		.param("productName", "qwerty")
		.param("productAddedBy", userid)
		.param("productCategory", "fashion")
		.param("productSubCategory", "shirts")
		.param("productPrice", "11500")
		.param("productDescription", "Addias Originals")
		.param("productFor", "women")
		.multiPart("productImage", new File("C:\\Users\\khitish\\Desktop\\WomanImage.jpg"));
		
		String create_product_response = create_product_main_request.when()
		.post("api/ecom/product/add-product")
		.then().extract().response().asString();
		
		//Create JsonPath to avoid POJO classes for simple Json structure
		JsonPath create_js = new JsonPath(create_product_response);
		String productid = create_js.getString("productId");
		
		System.out.println(productid);
		
/*-------------------------------------------------------------------Checkout-----------------------------------------------------------------------*/
		
		RequestSpecification checkout_request_base_spec =  new RequestSpecBuilder()
				.setBaseUri("https://rahulshettyacademy.com/")
				.setContentType(ContentType.JSON)
				.addHeader("Authorization", token)
				.build();
		
		//given->>implement Pjojo for request payload
		
		//First set all the element and then set the list
		CheckoutSubRequest checkout_sub = new CheckoutSubRequest();
		checkout_sub.setCountry("India");
		checkout_sub.setProductOrderedId(productid);
		
		//We created a list to push all items from CheckoutSubRequest class
		List<CheckoutSubRequest>list = new ArrayList<>();  
		list.add(checkout_sub);
		
		//Expects a list
		CheckoutRequest checkout_main = new CheckoutRequest();
		checkout_main.setOrders(list);
		
		
		RequestSpecification checkout_main_request = given()
		.spec(checkout_request_base_spec)
		.body(checkout_main);
		
		//response call
		String response_order = checkout_main_request.when().post("api/ecom/order/create-order")
								.then().log().all()
								.extract().response().asString();
		
/*-------------------------------------------------------------------Delete-------------------------------------------------------------------------*/
		
		RequestSpecification delete_request_base_spec =  new RequestSpecBuilder()
				.setBaseUri("https://rahulshettyacademy.com/")
				.setContentType(ContentType.JSON)
				.addHeader("Authorization", token)
				.build();
		
		RequestSpecification delete_request_main = given().spec(delete_request_base_spec).pathParam("productId", productid);
		
		String delete_response = delete_request_main.when().delete("api/ecom/product/delete-product/{productId}")
		.then()
		.extract().response().asString();
		
		JsonPath delete_js = new JsonPath(delete_response);
		String delete_msg = delete_js.getString("message");
		
		System.out.println(delete_msg);
	}

}
