package restApiTest;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RestApiTestCases {


	int idOfNewUser;
	String DOB;

	@Test
	public void addUser() throws ParseException
	{

		// creating the new user 

		RestAssured.baseURI="http://localhost:8080/api";
		String response =given().log().all()
				.headers("Content-Type","application/json")
				.body("{\r\n"
						+ "\"firstName\": \"Ashu\",\r\n"
						+ "\"lastName\": \"Gazta\",\r\n"
						+ "\"email\": \"newemail22@gmail.com\",\r\n"
						+ "\"dayOfBirth\": \"1991-07-01\"\r\n"
						+ "}")
				.when().post("/users")
				.then().log().all().assertThat().statusCode(201)
				.extract().asString();

		JsonPath js = new JsonPath(response);
		DOB=js.getString("dayOfBirth").replace("-", "/");
		String todaysDate=java.time.LocalDate.now().toString().replace("-", "/");


		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Date date1=sdf.parse(DOB);
		Date date2=sdf.parse(todaysDate);

		Assert.assertTrue(date1.before(date2), "DOB is less than todays date");

		idOfNewUser=js.getInt("id");



	}

	@Test
	public void changeUserDOB() {
		//using the put api to change DOB and verifying it
	
		RestAssured.baseURI="http://localhost:8080/api";
		String newres=given().log().all()
				.headers("Content-Type","application/json")
				.body("{\r\n"
						+ "\"firstName\": \"Ashu\",\r\n"
						+ "\"lastName\": \"Gazta\",\r\n"
						+ "\"email\": \"newemail22@gmail.com\",\r\n"
						+ "\"dayOfBirth\": \"1992-07-10\"\r\n"
						+ "}")
				.when().put("/users/{id}",idOfNewUser)
				.then().log().all().extract().asString();

		JsonPath js1 = new JsonPath(newres);
		String newDOB=js1.getString("dayOfBirth").replace("-", "/");

		System.out.println(newDOB);
		
		Assert.assertFalse(DOB.equals(newDOB), "DOB is changed");
	}

	@Test

	public void deleteuserById()
	{
		//deleting the newly created user 
		RestAssured.baseURI="http://localhost:8080/api";
		given().log().all()
		.headers("Content-Type","application/json")
		.when().delete("/users/{id}", idOfNewUser)
		.then().log().all().assertThat().statusCode(204) ;
	}

	@Test
	public void verifyDeleteduser() 
	{
		//Verifying that the user is deleted by calling the get user by id
		RestAssured.baseURI="http://localhost:8080/api";
		given().log().all()
		.headers("Content-Type","application/json")
		.when().get("/users/{id}", idOfNewUser)
		.then().log().all().assertThat().statusCode(404);
	}
	
}	