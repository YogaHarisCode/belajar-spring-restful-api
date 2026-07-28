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
import yogaharis.restful.model.CreateAddressRequest;
import yogaharis.restful.model.WebResponse;
import yogaharis.restful.model.AddressResponse;
import yogaharis.restful.repository.AddressRepository;
import yogaharis.restful.repository.ContactRepository;
import yogaharis.restful.repository.UserRepository;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerCreateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    private User userA;
    private User userB;
    private Contact contactOfUserA;

    @BeforeEach
    void setUp() {
        // bersihkan data lama biar test idempotent
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        // User A - pemilik contact yang valid
        userA = new User();
        userA.setUsername("usera");
        userA.setPassword("rahasia");
        userA.setName("User A");
        userA.setToken("token-user-a");
        userA.setExpiredAt(Instant.now().plusSeconds(3600).toEpochMilli());
        userRepository.save(userA);

        // User B - dipakai buat skenario contact bukan miliknya
        userB = new User();
        userB.setUsername("userb");
        userB.setPassword("rahasia");
        userB.setName("User B");
        userB.setToken("token-user-b");
        userB.setExpiredAt(Instant.now().plusSeconds(3600).toEpochMilli());
        userRepository.save(userB);

        contactOfUserA = new Contact();
        contactOfUserA.setId(UUID.randomUUID().toString());
        contactOfUserA.setFirstName("Budi");
        contactOfUserA.setLastName("Santoso");
        contactOfUserA.setEmail("budi@example.com");
        contactOfUserA.setPhone("08123456789");
        contactOfUserA.setUser(userA);
        contactRepository.save(contactOfUserA);
    }

    @Test
    void createAddressUnauthorized() throws Exception {
        CreateAddressRequest request = CreateAddressRequest.builder()
                .street("Jalan Sudirman No. 45")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("Indonesia")
                .postalCode("12190")
                .build();

        mockMvc.perform(
                post("/api/contacts/" + contactOfUserA.getId() + "/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                // sengaja tidak set header X-API-TOKEN
        ).andExpectAll(
                status().isUnauthorized()
        );
    }

    @Test
    void createAddressContactNotFound() throws Exception {
        CreateAddressRequest request = CreateAddressRequest.builder()
                .street("Jalan Sudirman No. 45")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("Indonesia")
                .postalCode("12190")
                .build();

        mockMvc.perform(
                post("/api/contacts/contact-id-yang-tidak-ada/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", userA.getToken())
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        );
    }

    @Test
    void createAddressAnotherUser() throws Exception {
        // contactOfUserA valid, tapi diakses pakai token userB
        CreateAddressRequest request = CreateAddressRequest.builder()
                .street("Jalan Sudirman No. 45")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("Indonesia")
                .postalCode("12190")
                .build();

        mockMvc.perform(
                post("/api/contacts/" + contactOfUserA.getId() + "/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", userB.getToken())
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        );
    }

    @Test
    void createAddressSuccess() throws Exception {
        CreateAddressRequest request = CreateAddressRequest.builder()
                .street("Jalan Sudirman No. 45")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("Indonesia")
                .postalCode("12190")
                .build();

        String responseBody = mockMvc.perform(
                post("/api/contacts/" + contactOfUserA.getId() + "/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", userA.getToken())
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk() // sesuaikan jadi isCreated() kalau nanti controller diubah ke 201
        ).andReturn().getResponse().getContentAsString();

        WebResponse<AddressResponse> response = objectMapper.readValue(
                responseBody,
                new TypeReference<>() {
                }
        );

        assertNotNull(response.getData());
        assertNotNull(response.getData().getId());
        assertEquals("Jalan Sudirman No. 45", response.getData().getStreet());
        assertEquals("Jakarta Selatan", response.getData().getCity());
        assertEquals("DKI Jakarta", response.getData().getProvince());
        assertEquals("Indonesia", response.getData().getCountry());
        assertEquals("12190", response.getData().getPostalCode());

        // pastikan benar-benar tersimpan & terhubung ke contact yang tepat
        assertTrue(addressRepository.findById(response.getData().getId()).isPresent());
        assertEquals(
                contactOfUserA.getId(),
                addressRepository.findById(response.getData().getId()).get().getContact().getId()
        );
    }
}