package io.github.svapollo.quarkussocial.rest;

import io.github.svapollo.quarkussocial.domain.model.Follower;
import io.github.svapollo.quarkussocial.domain.model.Post;
import io.github.svapollo.quarkussocial.domain.model.User;
import io.github.svapollo.quarkussocial.domain.repository.FollowerRepository;
import io.github.svapollo.quarkussocial.domain.repository.PostRepository;
import io.github.svapollo.quarkussocial.domain.repository.UserRepository;
import io.github.svapollo.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class) // substitui o @TestHTTPResource("/users")
class PostResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setUp() {
        //usuario padrao dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId  = user.getId();

        //cria um post para o usuario padrao
        Post post = new Post();
        post.setText("Hello, World!");
        post.setUser(user);
        postRepository.persist(post);

        //usuario que nao segue ninguem
        var userNotFollower = new User();
        userNotFollower.setAge(31);
        userNotFollower.setName("Fulano Teste");
        userRepository.persist(userNotFollower);
        userNotFollowerId  = userNotFollower.getId();

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
    @DisplayName("should create a post successfully")
    public void createPostTest() {

        var postRequest = new CreatePostRequest();
        postRequest.setText("Hello, World!");

        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParams("userId", userId)
        .when()
            .post()
        .then()
            .statusCode(201);
    }

    @Test
    @DisplayName("should return 404 when trying to make a post to a inexistent user")
    public void postForAnInexistentUserTest() {

        var postRequest = new CreatePostRequest();
        postRequest.setText("Hello, World!");

        var inexistentUserId = 99999;

        given()
                .contentType(ContentType.JSON)
                    .body(postRequest)
                    .pathParams("userId", inexistentUserId)
                .when()
                    .post()
                .then()
                    .statusCode(404);
    }

    @Test
    @DisplayName("should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest(){
        var inexistentUserId = 99999;

        given()
            .pathParams("userId", inexistentUserId)
        .when()
            .get()
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should return 400 when followerId header is not sent")
    public void listPostFollowerHeaderNotSentTest(){

            given()
                .pathParams("userId", userId)
            .when()
                .get()
            .then()
                .statusCode(400)
                .body(Matchers.is("You must provide a followerId header"));
    }

    @Test
    @DisplayName("should return 400 when follower doesn't exist")
    public void listPostFollowerNotFoundTest(){

        var inexistentFollowerId = 99999;

        given()
            .pathParams("userId", userId)
            .header("followerId", inexistentFollowerId)
        .when()
            .get()
        .then()
            .statusCode(400)
            .body(Matchers.is("Invalid followerId"));
    }

    @Test
    @DisplayName("should return 403 when follower isn't a follower")
    public void listPostNotAFollowerTest(){

        given()
                .pathParams("userId", userId)
                .header("followerId", userNotFollowerId)
        .when()
                .get()
        .then()
                .statusCode(403)
                .body(Matchers.is("You can't see posts from users you don't follow"));
    }

    @Test
    @DisplayName("should return posts successfully")
    public void listPostsTest(){
        given()
                .pathParams("userId", userId)
                .header("followerId", userFollowerId)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("size()",Matchers.is(1));
    }
}