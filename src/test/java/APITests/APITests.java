package APITests;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class APITests {

    static String authToken;
    static int publicPostId;
    static int userId, count;

    @BeforeTest
    public void loginTest() throws JsonProcessingException {

        LoginPOJO login = new LoginPOJO();
        login.setUsernameOrEmail("test0712");
        login.setPassword("Test0712");

        ObjectMapper objectMapper = new ObjectMapper();
        String convertLoginPojoToJSON = objectMapper.writeValueAsString(login);

        baseURI = "http://training.skillo-bg.com:3100";

        Response response = given()
                .header("Content-Type", "application/json")
                .body(convertLoginPojoToJSON)
                .when()
                .post("/users/login");

        response
                .then()
                .statusCode(201);

        String loginResponseBody = response.getBody().asString();
        authToken = JsonPath.parse(loginResponseBody).read("$.token");
        userId = JsonPath.parse(loginResponseBody).read("$.user.id");
    }

    @Test
    public void addPublicPost() {
        ActionsPOJO addPost = new ActionsPOJO();
        addPost.setCaption("Post content");
        addPost.setCoverUrl("https://i.imgur.com/2bvab7y.jpeg");
        addPost.setPostStatus("public");

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(addPost)
                .when()
                .post("/posts");

        response
                .then()
                .statusCode(201)
                .assertThat().body("user.id", equalTo(userId))
                .assertThat().body("caption", equalTo(addPost.getCaption()));

        String ResponseBody = response.getBody().asString();
        publicPostId = JsonPath.parse(ResponseBody).read("$.id");
    }

    @Test

    public void getUsersPosts() {
        ValidatableResponse validatableResponse = given()
                .param("postStatus", "public")
                .param("take", 10)
                .param("skip", 0)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/users/2557/posts")
                .then()
                .log()
                .all();

        //Not working
        ArrayList<Integer> returnedPostId = validatableResponse.extract().path("id");
        Assert.assertEquals(returnedPostId.get(0), publicPostId);
    }

    @Test

    public void getAllPublicPosts() {
        given()
                .param("take", 10)
                .param("skip", 0)
                .header("Content-Type", "application/json")
                .when()
                .get("/posts/public")
                .then()
                .log()
                .all();
    }

   @Test
    public void likePost() {
        // create an object of ActionsPOJO class and add value for the fields
        ActionsPOJO likePost = new ActionsPOJO();
        likePost.setAction("likePost");

       Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(likePost)
                .when()
                .patch("/posts/4800");

       response
                .then()
                .body("post.id", equalTo(4800))
                .log()
                .all();

       //Not working
       String ResponseBody = response.getBody().asString();
       count = JsonPath.parse(ResponseBody).read("$.post.likesCount");

    }

    @Test
    public void commentPost() {
        ActionsPOJO commentPost = new ActionsPOJO();
        commentPost.setContent("My New Comment!");

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(commentPost)
                .when()
                .post("/posts/4800/comment")
                .then()
                .body("content", equalTo("My New Comment!"))
                .log()
                .all()
                .statusCode(201);
    }


}
