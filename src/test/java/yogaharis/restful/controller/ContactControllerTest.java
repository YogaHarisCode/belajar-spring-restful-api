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
import yogaharis.restful.entity.Contact;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.ContactResponse;
import yogaharis.restful.model.CreateContactRequest;
import yogaharis.restful.model.WebResponse;
import yogaharis.restful.repository.ContactRepository;
import yogaharis.restful.repository.UserRepository;
import yogaharis.restful.service.ContactService;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        contactRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setPassword("test");
        user.setName("Test");
        user.setToken("test");
        user.setExpiredAt(Instant.now().plus(Duration.ofDays(30)).toEpochMilli());
        userRepository.save(user);
    }

    @Test
    void testCreateContactWithoutHeader() throws Exception {
        CreateContactRequest request = CreateContactRequest.builder()
                .firstName("test")
                .lastName("test")
                .email("test@example.com")
                .phone("090909090909")
                .build();

        mockMvc.perform(
                post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
            });
           assertNotNull(response.getErrors());
           assertEquals("Unauthorized", response.getErrors());

        });
    }

    @Test
    void testCreateContactBadRequest() throws Exception {
        CreateContactRequest request = CreateContactRequest.builder()
                .firstName("")
                .lastName("test")
                .email("test@example.com")
                .phone("090909090909")
                .build();

        mockMvc.perform(
                post("/api/contacts")
                        .header("X-API-TOKEN", "test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
            });
            assertNotNull(response.getErrors());
            assertEquals("first name cannot blank", response.getErrors());

        });
    }

    @Test
    void testCreateContactSuccess() throws Exception {
        CreateContactRequest request = CreateContactRequest.builder()
                .firstName("test")
                .lastName("test")
                .email("test@example.com")
                .phone("090909090909")
                .build();

        mockMvc.perform(
                post("/api/contacts")
                        .header("X-API-TOKEN", "test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getId());
            assertEquals(request.getFirstName(), response.getData().getFirstName());
            assertEquals(request.getLastName(), response.getData().getLastName());
            assertEquals(request.getPhone(), response.getData().getPhone());
            assertEquals(request.getEmail(), response.getData().getEmail());

            Contact contactDb = contactRepository.findById(response.getData().getId()).orElse(null);
            assertNotNull(contactDb);
            assertEquals(response.getData().getId(), contactDb.getId());
            assertEquals(response.getData().getFirstName(), contactDb.getFirstName());
            assertEquals(response.getData().getLastName(), contactDb.getLastName());
            assertEquals(response.getData().getEmail(), contactDb.getEmail());
            assertEquals(response.getData().getPhone(), contactDb.getPhone());

            assertNotNull(contactDb.getUser());
            assertEquals("test", contactDb.getUser().getUsername());
            assertEquals("test", contactDb.getUser().getPassword());
            assertEquals("Test", contactDb.getUser().getName());
            assertEquals("test", contactDb.getUser().getToken());
        });
    }
}