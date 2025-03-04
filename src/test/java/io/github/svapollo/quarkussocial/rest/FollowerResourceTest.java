package io.github.svapollo.quarkussocial.rest;

import io.github.svapollo.quarkussocial.domain.model.Follower;
import io.github.svapollo.quarkussocial.domain.model.User;
import io.github.svapollo.quarkussocial.domain.repository.FollowerRepository;
import io.github.svapollo.quarkussocial.domain.repository.UserRepository;
import io.github.svapollo.quarkussocial.rest.dto.CreateFollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    void setUp() {
        //usuario padrao dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId  = user.getId();

        //usuario que segue o usuario padrao
        var userFollower = new User();
        userFollower.setAge(32);
        userFollower.setName("Fulano Testinho");
        userRepository.persist(userFollower);
        userFollowerId  = userFollower.getId();

        //cria um seguidor
        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("Should return 409 when followerId is the same as userId")
    public void sameUserAsFollowerTest(){

        var body = new CreateFollowerRequest();
        body.setFollowerId(userId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(Response.Status.CONFLICT.getStatusCode())
            .body(Matchers.equalTo("You can't follow yourself"));
    }

    @Test
    @DisplayName("Should return 404 on follow a user when userId does not exist")
    public void userNotFoundWhenTryingToFollowTest(){

        var body = new CreateFollowerRequest();
        body.setFollowerId(userId);

        var inexistentUserId = 9999;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", inexistentUserId)
        .when()
                .put()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should follow a user successfully")
    public void followUserTest(){

        var body = new CreateFollowerRequest();
        body.setFollowerId(userFollowerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
        .when()
                .put()
        .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Should return 404 on list a user followers when userId does not exist")
    public void userNotFoundWhenListingFollowersTest(){

        var inexistentUserId = 9999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should list a user followers successfully")
    public void listingFollowersTest(){

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("userId", userId)
                .when()
                        .get()
                .then()
                        .extract().response();

        var followersCount = response.jsonPath().get("followersCount");

        assertEquals(1, followersCount);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 on unfollow a user when userId does not exist")
    public void userNotFoundWhenUnfollowingAUserTest(){

        var inexistentUserId = 9999;

        given()
                .pathParam("userId", inexistentUserId)
                .queryParam("followerId", userFollowerId)
        .when()
                .delete()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should Unfollow an user successfully")
    public void unfollowUserTest(){

        given()
                .pathParam("userId", userId)
                .queryParam("followerId", userFollowerId)
        .when()
                .delete()
        .then()
                .statusCode(204);
    }
}