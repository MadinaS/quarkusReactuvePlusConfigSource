package org.acme.config;

import static io.restassured.RestAssured.given;
//import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;

import javax.ws.rs.core.MediaType;

import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class AuthResourceTest {

    @Test
    public void loginUser() {

        given()
                .filter( ResponseLoggingFilter.logResponseIfStatusCodeIs(200))
                .body("{\"name\": \"Pear\", \"description\": \"Winter fruit\"}")
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .assertThat()
                .cookie("chatToken")
                .body(is("hello"));
    }




    @Test
    public void logoutUser() {

        given()
                .filter( ResponseLoggingFilter.logResponseIfStatusCodeIs(200))
                .header("Authorization", "Bearer eyJraWQiOiJrMSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJzdWJqZWN0IiwianRpIjoiSmciLCJleHAiOjE1OTYxNTc2ODYsImlhdCI6MTU5NjExNDQ4NiwibmJmIjoxNTk2MTE0MzY2LCJ1c2VyTG9naW4iOiJ1c2VyTG9naW4iLCJuYW1lIjoiZmlyc3ROYW1lIHN1cm5hbWUiLCJncm91cHMiOlsidXNlciJdLCJpc3MiOiJJc3N1ZXIifQ.IiqN5_moQo9T80jifmtFwkRCmpnw1Z55x62F9Pd-Cg-skLAgG-g_iC02JY03gSdcL95UOBaiqy2-K_SrEeuVkjYJB7uAjepFUcR41lgkavv_AK1FJ0Zmolcd-wkOTaV6aCs6C50HUFF2m5AZzl_DkNEqHHeKWUnGH8FvoICLwLGEGYNchXnxOP1X3t1yMm-d5lpNfvN-YVdn5LEuPAn7jzBN0vZ2Zs6g0KoCw-LkMAhq-SeGx1eIYExLUkWIUjEg35Xy_FBIxCassdze2APRahvTf86xiyQKjkiv2npMEa5U3u1CuWiXgtvNBgetirXZRXQI2zB8LtLid-drL6cYdA")
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/auth/logout")
                .then()
                .statusCode(200)
                .assertThat()
                .body(is("abgemeldet"));
    }
}
