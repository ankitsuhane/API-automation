package com.orgname.test.stepdefinitions.api;

import com.orgname.framework.api.ApiDriverFactory;
import cucumber.api.java8.En;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matcher;
import org.junit.Assert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.hasToString;

public class TestApidefinition extends ApiDriverFactory implements En {
    Response response =null;
    public TestApidefinition() {
        Given("^Hit the baseURL \"([^\"]*)\"$", (String baseURI) -> {
            RestAssured.baseURI = baseURI;
        });
        When("^run the GET method for the API endpoint \"([^\"]*)\"$", (String path) -> {
            response= getMethod(path,"");
            printOutput(response);
        });

        Then("^validate the base currency \"([^\"]*)\" and Equivalent \"([^\"]*)\" currencies are with \"([^\"]*)\" at the first\\.$", (String expectedBaseCurrency, String expectedCurrencyCount, String actualFirstCurrency) -> {
            String actualBaseCurrency = response.then().extract().response().jsonPath().getString("base.symbol");

            response.then().statusCode(HttpStatus.SC_OK).
                    body("base.symbol", hasToString("SAR"),
                            "equivalent.size", hasToString("11"));

            //https://www.youtube.com/watch?v=W409AAKTpcc
            String symbol= response.path("equivalent.find {it.symbol =='SAR'}.name");
            List<String> symbols  = response.path("equivalent.findAll {it.symbol =='SAR'}.name");
            List<?> decimal_digits  = response.path("equivalent.findAll {it.decimal_digits >2 }.name");
            HashMap<String, ?> maxRate =response.path("equivalent.max{it.Rate}");
            String maxRateName =response.path("equivalent.max{it.Rate}.name");
            String minRateName =response.path("equivalent.min{it.Rate}.name");
            double sumRate =response.path("equivalent.sum {it.rate}");
            List<String> sumNameCaps =response.path
                    ("equivalent.findAll{it.decimal_digits}.collect{it.name.toUpperCase()}");
            List<String> sumNameConcat =response.path
                    ("equivalent.findAll{it.decimal_digits}.collect{it.name+' Ankit'}");
            List<String> sumNameLen =response.path
                    ("equivalent.findAll{it.decimal_digits>2}.findAll{it.name.length()>12}");
            System.out.println(symbol + symbols+decimal_digits+ maxRateName+sumRate+sumNameCaps+
                    sumNameConcat+sumNameLen);

            Assert.assertEquals(expectedBaseCurrency, actualBaseCurrency);
            ArrayList<String> actualCurrencyCount= response.then().extract().response().jsonPath().get("equivalent.symbol");
            Assert.assertEquals(Integer.parseInt(expectedCurrencyCount), actualCurrencyCount.size());
            String expectedFirstCurrency= response.then().extract().response().jsonPath().get("equivalent.symbol[0]");
            Assert.assertEquals(expectedFirstCurrency, actualFirstCurrency);
        });

        When("^run the POST method for the API endpoint \"([^\"]*)\" for checkinDate \"([^\"]*)\" checkoutDate \"([^\"]*)\"$", (String path, String checkinDate, String checkoutDate) -> {
            String requestBody ="{\"searchCriteria\":[{\"lookupTypeId\":2,\"lookupId\":[9]}],\"" +
                    "checkIn\":\""+checkinDate+"\"," +
                    "\"checkOut\":\""+checkoutDate+"\",\"" +
                    "sortBy\":\"rank\",\"sortOrder\":\"DESC\",\"rankType\":\"dynamic\",\"pageNo\":1,\"pageSize\":10}";
            response= postMethod(path,requestBody).then().extract().response();
            printOutput(response);
        });

        Then("^validate a hotel name \"([^\"]*)\" and min and max bathroom is \"([^\"]*)\" \"([^\"]*)\" in filter$", (String expHotelName, String expMinBathroom, String expMaxBathroom) -> {
            ArrayList<String> actHotelNames = response.path("properties.nameEn");
            int actMinBathroom = response.path("filters.bathRoomCountFilter.minimumCount");
            int actMaxBathroom = response.path("filters.bathRoomCountFilter.maximumCount");
            String actHotelName =null;
            for (String hotelName : actHotelNames) {
                actHotelName = hotelName;
                if (actHotelName.equalsIgnoreCase(expHotelName))
                    break;
            }
            Assert.assertEquals(expHotelName,actHotelName);
            Assert.assertEquals(Integer.parseInt(expMinBathroom),actMinBathroom);
            Assert.assertEquals(Integer.parseInt(expMaxBathroom),actMaxBathroom);
        });
    }
}
