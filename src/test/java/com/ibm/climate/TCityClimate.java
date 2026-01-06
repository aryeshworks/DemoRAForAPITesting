package com.ibm.climate;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

public class TCityClimate {
    @Test
    void getCityClimateDetails(){
        RestAssured.baseURI="https://demoqa.com/utilities/weather/city";
        RequestSpecification req = RestAssured.given();

        Response res = req.request(Method.GET, "/kolkata");
        String output = res.body().asString();
        System.out.println(output + "\t" + res.statusCode());

        Headers headers = res.headers();
        headers.forEach(System.out::println);
    }
}
