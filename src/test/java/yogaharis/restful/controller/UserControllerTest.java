package yogaharis.restful.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.*;
import yogaharis.restful.repository.UserRepository;
import yogaharis.restful.security.BCrypt;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterUserRequest userRequest = new RegisterUserRequest();
        userRequest.setName("Yoga");
        userRequest.setUsername("yoga");
        userRequest.setPassword("rahasia");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))
        ).andExpectAll(
                status().isCreated()
        ).andExpect(result -> {
            WebResponse<UserResponse> userServiceWebResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(userServiceWebResponse.getData());

            assertNull(userServiceWebResponse.getErrors());

            User user = userRepository.findById("yoga").orElse(null);
            assertNotNull(user);
            assertEquals(userRequest.getUsername(), user.getUsername());
            assertEquals(userRequest.getName(), user.getName());
            assertTrue(BCrypt.checkpw(userRequest.getPassword(), user.getPassword()));
        });
    }

    @Test
    void testRegisterBadRequest() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setName("");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testRegisterDuplicate() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        userRepository.save(user);

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("test");
        request.setPassword("rahasia");
        request.setName("Test");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
            assertEquals("username already exist", response.getErrors());
        });
    }

    @Test
    void testLoginSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        userRepository.save(user);

        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .username("test")
                .password("test")
                .build();

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenResponse>>() {
            });

            assertNull(response.getErrors());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());

            User userDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDb);
            assertEquals(userDb.getToken(), response.getData().getToken());
            assertEquals(userDb.getExpiredAt(), response.getData().getExpiredAt());
        });
    }

    @Test
    void testLoginUserNotFound() throws Exception {
        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .username("test")
                .password("test")
                .build();

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenResponse>>() {
            });

            assertNull(response.getData());
            assertNotNull(response.getErrors());
            assertEquals("Username or Password wrong", response.getErrors());
        });
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        userRepository.save(user);

        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .username("test")
                .password("salah")
                .build();

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenResponse>>() {
            });

            assertNull(response.getData());
            assertNotNull(response.getErrors());
            assertEquals("Username or Password wrong", response.getErrors());
        });
    }

    @Test
    void testGetUserNotSendToken() throws Exception {
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });

            assertNotNull(response.getErrors());
            assertEquals("Unauthorized", response.getErrors());
        });
    }

    @Test
    void testGetUserNotValidToken() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setExpiredAt(Instant.now().plus(Duration.ofDays(30)).toEpochMilli());
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "salah")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });

            assertNotNull(response.getErrors());
            assertEquals("Unauthorized", response.getErrors());
        });
    }

    @Test
    void testGetUserExpiredToken() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setExpiredAt(Instant.now().minus(Duration.ofDays(1)).toEpochMilli());
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });

            assertNotNull(response.getErrors());
            assertEquals("Unauthorized", response.getErrors());
        });
    }

    @Test
    void testGetUserSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setExpiredAt(Instant.now().plus(Duration.ofDays(30)).toEpochMilli());
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });

            assertNull(response.getErrors());
            assertNotNull(response.getData());
            assertEquals("Test", response.getData().getName());
            assertEquals("test", response.getData().getUsername());
        });
    }

    @Test
    void testUpdateUserNotSendToken() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setExpiredAt(Instant.now().plus(Duration.ofDays(30)).toEpochMilli());
        userRepository.save(user);

        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Test Update")
                .password("test update")
                .build();

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });

            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateUserNotValidToken() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setExpiredAt(Instant.now().plus(Duration.ofDays(30)).toEpochMilli());
        userRepository.save(user);

        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Test Update")
                .password("test update")
                .build();

        mockMvc.perform(
                patch("/api/users/current")
                        .header("X-API-TOKEN", "salah")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });

            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateUserExpiredToken() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setExpiredAt(Instant.now().minus(Duration.ofSeconds(30)).toEpochMilli());
        userRepository.save(user);

        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Test Update")
                .password("test update")
                .build();

        mockMvc.perform(
                patch("/api/users/current")
                        .header("X-API-TOKEN", "test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });

            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateUserName() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setExpiredAt(Instant.now().plus(Duration.ofDays(30)).toEpochMilli());
        userRepository.save(user);

        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Test Update")
                .build();

        mockMvc.perform(
                patch("/api/users/current")
                        .header("X-API-TOKEN", "test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });

            assertNull(response.getErrors());
            assertNotNull(response.getData());

            User userDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDb);
            assertEquals(request.getName(), userDb.getName());
        });
    }

    @Test
    void testUpdateUserPassword() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setExpiredAt(Instant.now().plus(Duration.ofDays(30)).toEpochMilli());
        userRepository.save(user);

        UpdateUserRequest request = UpdateUserRequest.builder()
                .password("testupdate")
                .build();

        mockMvc.perform(
                patch("/api/users/current")
                        .header("X-API-TOKEN", "test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });

            assertNull(response.getErrors());
            assertNotNull(response.getData());

            User userDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDb);
            assertTrue(BCrypt.checkpw(request.getPassword(), userDb.getPassword()));
        });
    }

    @Test
    void testUpdateUserNameAndPassword() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setExpiredAt(Instant.now().plus(Duration.ofDays(30)).toEpochMilli());
        userRepository.save(user);

        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Test Update")
                .password("testupdate")
                .build();

        mockMvc.perform(
                patch("/api/users/current")
                        .header("X-API-TOKEN", "test")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });

            assertNull(response.getErrors());
            assertNotNull(response.getData());

            User userDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDb);
            assertEquals(request.getName(), userDb.getName());
            assertTrue(BCrypt.checkpw(request.getPassword(), userDb.getPassword()));
        });
    }
}