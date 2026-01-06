package com.ibm.stocks;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

public class TStocksOps {
    RequestSpecification req;
    String res;

    @BeforeMethod
    public void localSetup() {
        req = RestAssured.given().baseUri("http://localhost:3000").basePath("/stocks").contentType("application/json");
    }

    @Test(priority = 1)
    public void testAddStock() {
        String requestBody = """
                {
                    "id": "7",
                    "name": "Flipkart",
                    "price": 75.40
                }
                """;

        res = req.body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(201)
//                test properties
                .body("$", hasKey("id"))
                .body("$", hasKey("name"))
                .body("$", hasKey("price"))
                .body("price", greaterThan(0f))
//                test exact values
                .body("id", equalTo("7"))
                .body("name", equalTo("Flipkart"))
                .body("price", equalTo(75.40f))
//                convert
                .extract()
                .asPrettyString();

        System.out.println("[ADD STOCK] Response: " + res);
    }

    @Test(priority = 2, dependsOnMethods = "testAddStock")
    public void testUpdateStock() {
        String requestBody = """
                {
                    "name": "Flipkart23",
                    "price": 99.99
                }
                """;

        res = req.pathParam("id", "7").body(requestBody).when().put("/{id}").then().statusCode(200).body("name", equalTo("Flipkart23")).body("price", equalTo(99.99f)).extract().asPrettyString();
        System.out.println("[UPDATE STOCK] Response: " + res);
    }

    @Test(priority=3, dependsOnMethods = "testAddStock")
    public void testPartialUpdateStock() {
        String requestBody = """
                {
                    "name": "Flipkart"
                }
                """;
        res = req.pathParam("id", "7").body(requestBody).when().patch("/{id}").then().statusCode(200).body("name", equalTo("Flipkart")).body("price", equalTo(99.99f)).extract().asPrettyString();
        System.out.println("[PARTIAL UPDATE STOCK] Response: " + res);
    }

    @Test(priority = 4)
    public void testDeleteStock() {
        res = req.pathParam("id", "3").when().delete("/{id}").then().assertThat().statusCode(200).extract().asPrettyString();
        System.out.println("[DELETE STOCK] Response: " + res);
        req.pathParam("id", "3")
                .when()
                .get("/{id}")
                .then()
                .statusCode(anyOf(is(404), is(204)));
    }

    @Test(priority = 5)
    public void testGetStocks() {
        res = req.when().get().then().assertThat().statusCode(200)
                // collection check
                .body("size()", greaterThan(0))

                // test structure of first item
                .body("[0].id", notNullValue())
                .body("[0].name", notNullValue())
                .body("[0].price", instanceOf(Number.class))

                // business rule
                .body("[0].price", greaterThan(0f))

                .extract().asPrettyString();
        System.out.println("[GET ALL STOCKS] Response: " + res);
    }

    @Test(priority = 0)
    public void testGetStockById() {
        res = req.pathParam("id", "3").when().get("{id}").then().assertThat().statusCode(200)
                // test structure
                .body("$", hasKey("id"))
                .body("$", hasKey("name"))
                .body("$", hasKey("price"))

                // test types
                .body("id", instanceOf(String.class))
                .body("name", instanceOf(String.class))
                .body("price", instanceOf(Number.class))

                .body("price", greaterThan(0f))

                .extract().asPrettyString();
        System.out.println("[GET STOCK BY ID] Response: " + res);
    }
}
