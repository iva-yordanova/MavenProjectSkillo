package APITests;

import com.jayway.jsonpath.JsonPath;
import io.restassured.response.*;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class APITests {

    static String authToken;
    static Integer publicPostId;
    static Integer userId, count, commentId;
    static String BirthDate;
    static String milliseconds;

    //Set baseURI

    @BeforeClass
    public void setup() {
        baseURI = "http://training.skillo-bg.com:3100";
    }

    static SignUpPOJO signUp = new SignUpPOJO();

    //Register new user

    @Test(priority = -1)
    public void signUp() {
        Date date = new Date();
        SimpleDateFormat DateFor = new SimpleDateFormat("dd.MM.yyyy");
        BirthDate = DateFor.format(date);
        String currentDate = String.valueOf(date.getTime());
        milliseconds = currentDate.substring(9, 13);
        baseURI = "http://training.skillo-bg.com:3100";

        signUp.setUsername("test" + milliseconds);
        signUp.setEmail("test" + milliseconds + "@abv.bg");
        signUp.setBirthDate(BirthDate);
        signUp.setPassword("test" + milliseconds);
        signUp.setPublicInfo("test" + milliseconds);
        Response response = given()
                .header("Content-Type", "application/json")
                .body(signUp)
                .when()
                .post(baseURI + "/users");
        response
                .then()
                .statusCode(201);
    }

    //Log in with the registered user

    @Test(priority = 1)
    public void loginTest() {

        LoginPOJO login = new LoginPOJO();
        login.setUsernameOrEmail(signUp.getUsername());
        login.setPassword(signUp.getPassword());

        Response response = given()
                .header("Content-Type", "application/json")
                .body(login)
                .when()
                .post(baseURI + "/users/login");

        response
                .then()
                .statusCode(201);

        String loginResponseBody = response.getBody().asString();
        authToken = JsonPath.parse(loginResponseBody).read("$.token");
        userId = JsonPath.parse(loginResponseBody).read("$.user.id");
    }

    //Edit user's info

    @Test(priority = 2)
    public void editProfile() {
        signUp.setPublicInfo("update" + milliseconds);
        signUp.setProfilePicUrl("https://i.imgur.com/ShkOiCf.jpeg");

        ValidatableResponse validatableResponse = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(signUp)
                .when()
                .put(baseURI + "/users")
                .then()
                .log()
                .body()
                .statusCode(200)
                .assertThat().body("user.publicInfo", equalTo(signUp.getPublicInfo()))
                .assertThat().body("user.profilePicUrl", equalTo(signUp.getProfilePicUrl()));
    }

    //Add public post

    @Test (priority=2)
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
                .log()
                .all()
                .statusCode(201)
                .assertThat().body("user.id", equalTo(userId))
                .body("caption", equalTo(addPost.getCaption()));

        String ResponseBody = response.getBody().asString();
        publicPostId = JsonPath.parse(ResponseBody).read("$.id");
    }

    //Check user's posts
    @Test (priority=3)

    public void getUsersPosts() {
        ValidatableResponse validatableResponse = given()
                .param("postStatus", "public")
                .param("take", 20)
                .param("skip", 0)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/users/"+userId+"/posts")
                .then()
                .log()
                .all();

        ArrayList<Integer> returnedPostId = validatableResponse.extract().path("id");
        Collections.sort(returnedPostId, Collections.reverseOrder());
        Assert.assertEquals(returnedPostId.get(0), publicPostId);
    }

    //Like and dislike a post

    @Test(priority=3, groups="LikePost")
    public void likePost() {
        // create an object of ActionsPOJO class and add value for the fields
        ActionsPOJO likePost = new ActionsPOJO();
        likePost.setAction("likePost");

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(likePost)
                .when()
                .patch("/posts/"+publicPostId);

        String ResponseBody = response.getBody().asString();
        count = JsonPath.parse(ResponseBody).read("$.post.likesCount");

        response
                .then()
                .body("post.id", equalTo(publicPostId))
                .body("post.likesCount", equalTo(count));
    }

    @Test(priority=3, dependsOnGroups = "LikePost")
    public void unLikePost() {
        // create an object of ActionsPOJO class and add value for the fields
        ActionsPOJO likePost = new ActionsPOJO();
        likePost.setAction("likePost");

        Response response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(likePost)
                .when()
                .patch("/posts/"+publicPostId);

        response
                .then()
                .body("post.id", equalTo(publicPostId))
                .body("post.likesCount", lessThan(count));
    }

    //Comment a post and delete it

    @Test(priority=3, groups="Comment")
    public void commentPost() {
        ActionsPOJO commentPost = new ActionsPOJO();
        commentPost.setContent("My New Comment!");

        ValidatableResponse validatableResponse=given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .body(commentPost)
                .when()
                .post(baseURI+"/posts/" + publicPostId+"/comment")
                .then()
                .body("content", equalTo("My New Comment!"))
                .log()
                .all()
                .statusCode(201);

        commentId = validatableResponse.extract().path("id");
    }

    @Test(priority=3, dependsOnGroups = "Comment")
    public void deleteComment() {

        given()
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + authToken)
            .when()
            .delete(baseURI+ "/posts/" + publicPostId+"/comments/"+commentId)
            .then()
            .statusCode(200);
    }

    @Test(priority=4)

    public void deletePost(){
        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete(baseURI + "/posts/" +publicPostId)
                .then()
                .statusCode(200);
    }

    @Test(priority=5)

    public void noPosts(){

        ValidatableResponse response = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken)
                .param("postStatus", "public")
                .param("take", 20)
                .param("skip", 0)
                .when()
                .get(baseURI+"/users/"+userId+"/posts")
                .then()
                .log()
                .all()
                .assertThat().body("", Matchers.empty());
    }


    @AfterClass(description = "delete user")
    public void deleteUser() {

        // delete the user
        Response deleteResponse = given()
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .when()
                .delete(baseURI + "/users/"+userId);

        deleteResponse
                .then()
                .log()
                .body()
                .statusCode(200);
    }

}
