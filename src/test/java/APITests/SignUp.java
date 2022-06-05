package APITests;

import io.restassured.response.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.text.SimpleDateFormat;
import java.util.Date;
import static io.restassured.RestAssured.*;



public class SignUp {
    @BeforeClass
    public void setup(){

        baseURI = "http://training.skillo-bg.com:3100";
    }
    static SignUpPOJO signUp=new SignUpPOJO();
    static String BirthDate;

    @Test(priority = -1)
    public void signUp() {
        Date date = new Date();
        SimpleDateFormat DateFor = new SimpleDateFormat("dd.MM.yyyy");
        BirthDate = DateFor.format(date);
        String currentDate= String.valueOf(date.getTime());
        String milliseconds=currentDate.substring(9,13);

        signUp.setUsername("test" + milliseconds);
        signUp.setEmail("test" + milliseconds + "@abv.bg");
        signUp.setBirthDate(BirthDate);
        signUp.setPassword("test"+milliseconds);
        signUp.setPublicInfo("test" +milliseconds);
        Response response = given()
                .header("Content-Type", "application/json")
                .body(signUp)
                .when()
                .post(baseURI + "/users");
        response
                .then()
                .log()
                .body()
                .statusCode(201);
    }
}
