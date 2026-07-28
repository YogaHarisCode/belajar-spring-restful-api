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
import yogaharis.restful.entity.Address;
import yogaharis.restful.entity.Contact;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.AddressResponse;
import yogaharis.restful.model.CreateAddressRequest;
import yogaharis.restful.model.UpdateAddressRequest;
import yogaharis.restful.model.WebResponse;
import yogaharis.restful.repository.AddressRepository;
import yogaharis.restful.repository.ContactRepository;
import yogaharis.restful.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Contact contact;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();

        // User A yang login
        user = new User();
        user.setUsername("yoga");
        user.setPassword("rahasia");
        user.setName("Yoga Haris");
        user.setToken("token-yoga");
        user.setExpiredAt(Instant.now().plusSeconds(3600).toEpochMilli());
        userRepository.save(user);

        // Contact milik User A
        contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName("Budi");
        contact.setLastName("Santoso");
        contact.setEmail("budi@example.com");
        contact.setPhone("081234567890");
        contact.setUser(user);
        contactRepository.save(contact);
    }

    // ==================== CREATE ADDRESS ====================

    @Test
    void testCreateAddressUnauthorized() throws Exception {
        CreateAddressRequest request = CreateAddressRequest.builder()
                .street("Jalan Sudirman No. 45")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("Indonesia")
                .postalCode("12190")
                .build();

        mockMvc.perform(
                post("/api/contacts/" + contact.getId() + "/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testCreateAddressContactNotFound() throws Exception {
        CreateAddressRequest request = CreateAddressRequest.builder()
                .street("Jalan Sudirman No. 45")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("Indonesia")
                .postalCode("12190")
                .build();

        String contactIdTidakAda = UUID.randomUUID().toString();

        mockMvc.perform(
                post("/api/contacts/" + contactIdTidakAda + "/addresses")
                        .header("X-API-TOKEN", "token-yoga")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testCreateAddressAnotherUser() throws Exception {
        User anotherUser = new User();
        anotherUser.setUsername("budi");
        anotherUser.setPassword("rahasia");
        anotherUser.setName("Budi Lain");
        anotherUser.setToken("token-budi");
        anotherUser.setExpiredAt(Instant.now().plusSeconds(3600).toEpochMilli());
        userRepository.save(anotherUser);

        CreateAddressRequest request = CreateAddressRequest.builder()
                .street("Jalan Sudirman No. 45")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("Indonesia")
                .postalCode("12190")
                .build();

        mockMvc.perform(
                post("/api/contacts/" + contact.getId() + "/addresses")
                        .header("X-API-TOKEN", "token-budi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testCreateAddressSuccess() throws Exception {
        CreateAddressRequest request = CreateAddressRequest.builder()
                .street("Jalan Sudirman No. 45")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("Indonesia")
                .postalCode("12190")
                .build();

        mockMvc.perform(
                post("/api/contacts/" + contact.getId() + "/addresses")
                        .header("X-API-TOKEN", "token-yoga")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertNull(response.getErrors());
            assertNotNull(response.getData().getId());
            assertEquals("Jalan Sudirman No. 45", response.getData().getStreet());
            assertEquals("Jakarta Selatan", response.getData().getCity());
            assertEquals("DKI Jakarta", response.getData().getProvince());
            assertEquals("Indonesia", response.getData().getCountry());
            assertEquals("12190", response.getData().getPostalCode());

            assertTrue(addressRepository.existsById(response.getData().getId()));
            addressRepository.findById(response.getData().getId()).ifPresent(address ->
                    assertEquals(contact.getId(), address.getContact().getId()));
        });
    }

    // ==================== GET ADDRESS ====================

    @Test
    void testGetAddressUnauthorized() throws Exception {
        Address address = createTestAddress(contact);

        mockMvc.perform(
                get("/api/contacts/" + contact.getId() + "/addresses/" + address.getId())
                // tanpa header X-API-TOKEN
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetAddressContactNotFound() throws Exception {
        Address address = createTestAddress(contact);

        String contactIdTidakAda = UUID.randomUUID().toString();

        mockMvc.perform(
                get("/api/contacts/" + contactIdTidakAda + "/addresses/" + address.getId())
                        .header("X-API-TOKEN", "token-yoga")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetAddressNotFound() throws Exception {
        // Contact lain milik user yang sama, addressId tidak berelasi ke contact ini
        Contact anotherContact = new Contact();
        anotherContact.setId(UUID.randomUUID().toString());
        anotherContact.setFirstName("Contact");
        anotherContact.setLastName("Lain");
        anotherContact.setEmail("contactlain@example.com");
        anotherContact.setPhone("081200000000");
        anotherContact.setUser(user);
        contactRepository.save(anotherContact);

        // address ini milik "contact", bukan "anotherContact"
        Address address = createTestAddress(contact);

        mockMvc.perform(
                get("/api/contacts/" + anotherContact.getId() + "/addresses/" + address.getId())
                        .header("X-API-TOKEN", "token-yoga")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetAddressSuccess() throws Exception {
        Address address = createTestAddress(contact);

        mockMvc.perform(
                get("/api/contacts/" + contact.getId() + "/addresses/" + address.getId())
                        .header("X-API-TOKEN", "token-yoga")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertNull(response.getErrors());
            assertEquals(address.getId(), response.getData().getId());
            assertEquals(address.getStreet(), response.getData().getStreet());
            assertEquals(address.getCity(), response.getData().getCity());
            assertEquals(address.getProvince(), response.getData().getProvince());
            assertEquals(address.getCountry(), response.getData().getCountry());
            assertEquals(address.getPostalCode(), response.getData().getPostalCode());
        });
    }

    // ==================== UPDATE ADDRESS ====================

    @Test
    void testUpdateAddressUnauthorized() throws Exception {
        Address address = createTestAddress(contact);

        UpdateAddressRequest request = UpdateAddressRequest.builder()
                .street("Jalan Gatot Subroto No. 10")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("Indonesia")
                .postalCode("12930")
                .build();

        mockMvc.perform(
                put("/api/contacts/" + contact.getId() + "/addresses/" + address.getId())
                        // tanpa header X-API-TOKEN
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateAddressContactNotFound() throws Exception {
        Address address = createTestAddress(contact);

        String contactIdTidakAda = UUID.randomUUID().toString();

        UpdateAddressRequest request = UpdateAddressRequest.builder()
                .street("Jalan Gatot Subroto No. 10")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("Indonesia")
                .postalCode("12930")
                .build();

        mockMvc.perform(
                put("/api/contacts/" + contactIdTidakAda + "/addresses/" + address.getId())
                        .header("X-API-TOKEN", "token-yoga")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateAddressNotFound() throws Exception {
        // Contact lain milik user yang sama, addressId tidak berelasi ke contact ini
        Contact anotherContact = new Contact();
        anotherContact.setId(UUID.randomUUID().toString());
        anotherContact.setFirstName("Contact");
        anotherContact.setLastName("Lain");
        anotherContact.setEmail("contactlain@example.com");
        anotherContact.setPhone("081200000000");
        anotherContact.setUser(user);
        contactRepository.save(anotherContact);

        // address ini milik "contact", bukan "anotherContact"
        Address address = createTestAddress(contact);

        UpdateAddressRequest request = UpdateAddressRequest.builder()
                .street("Jalan Gatot Subroto No. 10")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("Indonesia")
                .postalCode("12930")
                .build();

        mockMvc.perform(
                put("/api/contacts/" + anotherContact.getId() + "/addresses/" + address.getId())
                        .header("X-API-TOKEN", "token-yoga")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateAddressAnotherUser() throws Exception {
        User anotherUser = new User();
        anotherUser.setUsername("budi");
        anotherUser.setPassword("rahasia");
        anotherUser.setName("Budi Lain");
        anotherUser.setToken("token-budi");
        anotherUser.setExpiredAt(Instant.now().plusSeconds(3600).toEpochMilli());
        userRepository.save(anotherUser);

        Address address = createTestAddress(contact);

        UpdateAddressRequest request = UpdateAddressRequest.builder()
                .street("Jalan Gatot Subroto No. 10")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("Indonesia")
                .postalCode("12930")
                .build();

        // User B mencoba update address milik contact User A
        mockMvc.perform(
                put("/api/contacts/" + contact.getId() + "/addresses/" + address.getId())
                        .header("X-API-TOKEN", "token-budi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateAddressBadRequest() throws Exception {
        Address address = createTestAddress(contact);

        UpdateAddressRequest request = UpdateAddressRequest.builder()
                .street("Jalan Gatot Subroto No. 10")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("") // kosong, melanggar @NotBlank
                .postalCode("12930")
                .build();

        mockMvc.perform(
                put("/api/contacts/" + contact.getId() + "/addresses/" + address.getId())
                        .header("X-API-TOKEN", "token-yoga")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateAddressSuccess() throws Exception {
        Address address = createTestAddress(contact);

        UpdateAddressRequest request = UpdateAddressRequest.builder()
                .street("Jalan Gatot Subroto No. 10")
                .city("Jakarta Selatan")
                .province("DKI Jakarta")
                .country("Indonesia")
                .postalCode("12930")
                .build();

        mockMvc.perform(
                put("/api/contacts/" + contact.getId() + "/addresses/" + address.getId())
                        .header("X-API-TOKEN", "token-yoga")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertNull(response.getErrors());
            assertEquals(address.getId(), response.getData().getId());
            assertEquals("Jalan Gatot Subroto No. 10", response.getData().getStreet());
            assertEquals("Jakarta Selatan", response.getData().getCity());
            assertEquals("DKI Jakarta", response.getData().getProvince());
            assertEquals("Indonesia", response.getData().getCountry());
            assertEquals("12930", response.getData().getPostalCode());

            // pastikan data di database benar-benar berubah
            addressRepository.findById(response.getData().getId()).ifPresent(updated -> {
                assertEquals("Jalan Gatot Subroto No. 10", updated.getStreet());
                assertEquals("12930", updated.getPostalCode());
            });
        });
    }

    // ==================== REMOVE ADDRESS ====================

    @Test
    void testRemoveAddressUnauthorized() throws Exception {
        Address address = createTestAddress(contact);

        mockMvc.perform(
                delete("/api/contacts/" + contact.getId() + "/addresses/" + address.getId())
                // tanpa header X-API-TOKEN
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testRemoveAddressContactNotFound() throws Exception {
        Address address = createTestAddress(contact);

        String contactIdTidakAda = UUID.randomUUID().toString();

        mockMvc.perform(
                delete("/api/contacts/" + contactIdTidakAda + "/addresses/" + address.getId())
                        .header("X-API-TOKEN", "token-yoga")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });

        // pastikan data tidak ikut terhapus karena request gagal
        assertTrue(addressRepository.existsById(address.getId()));
    }

    @Test
    void testRemoveAddressNotFound() throws Exception {
        // Contact lain milik user yang sama, addressId tidak berelasi ke contact ini
        Contact anotherContact = new Contact();
        anotherContact.setId(UUID.randomUUID().toString());
        anotherContact.setFirstName("Contact");
        anotherContact.setLastName("Lain");
        anotherContact.setEmail("contactlain@example.com");
        anotherContact.setPhone("081200000000");
        anotherContact.setUser(user);
        contactRepository.save(anotherContact);

        // address ini milik "contact", bukan "anotherContact"
        Address address = createTestAddress(contact);

        mockMvc.perform(
                delete("/api/contacts/" + anotherContact.getId() + "/addresses/" + address.getId())
                        .header("X-API-TOKEN", "token-yoga")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });

        assertTrue(addressRepository.existsById(address.getId()));
    }

    @Test
    void testRemoveAddressAnotherUser() throws Exception {
        User anotherUser = new User();
        anotherUser.setUsername("budi");
        anotherUser.setPassword("rahasia");
        anotherUser.setName("Budi Lain");
        anotherUser.setToken("token-budi");
        anotherUser.setExpiredAt(Instant.now().plusSeconds(3600).toEpochMilli());
        userRepository.save(anotherUser);

        Address address = createTestAddress(contact);

        // User B mencoba hapus address milik contact User A
        mockMvc.perform(
                delete("/api/contacts/" + contact.getId() + "/addresses/" + address.getId())
                        .header("X-API-TOKEN", "token-budi")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });

        assertTrue(addressRepository.existsById(address.getId()));
    }

    @Test
    void testRemoveAddressSuccess() throws Exception {
        Address address = createTestAddress(contact);

        mockMvc.perform(
                delete("/api/contacts/" + contact.getId() + "/addresses/" + address.getId())
                        .header("X-API-TOKEN", "token-yoga")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertNull(response.getErrors());
            assertEquals("OK", response.getData());
        });

        // pastikan data benar-benar terhapus permanen dari database
        assertFalse(addressRepository.existsById(address.getId()));
    }

    // ==================== LIST ADDRESS ====================

    @Test
    void testListAddressUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/contacts/" + contact.getId() + "/addresses")
                // tanpa header X-API-TOKEN
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testListAddressContactNotFound() throws Exception {
        String contactIdTidakAda = UUID.randomUUID().toString();

        mockMvc.perform(
                get("/api/contacts/" + contactIdTidakAda + "/addresses")
                        .header("X-API-TOKEN", "token-yoga")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testListAddressAnotherUser() throws Exception {
        User anotherUser = new User();
        anotherUser.setUsername("budi");
        anotherUser.setPassword("rahasia");
        anotherUser.setName("Budi Lain");
        anotherUser.setToken("token-budi");
        anotherUser.setExpiredAt(Instant.now().plusSeconds(3600).toEpochMilli());
        userRepository.save(anotherUser);

        createTestAddress(contact);

        // User B mencoba list address milik contact User A
        mockMvc.perform(
                get("/api/contacts/" + contact.getId() + "/addresses")
                        .header("X-API-TOKEN", "token-budi")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testListAddressSuccessEmpty() throws Exception {
        // contact belum punya address sama sekali
        mockMvc.perform(
                get("/api/contacts/" + contact.getId() + "/addresses")
                        .header("X-API-TOKEN", "token-yoga")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<AddressResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertNull(response.getErrors());
            assertNotNull(response.getData());
            assertTrue(response.getData().isEmpty());
        });
    }

    @Test
    void testListAddressSuccess() throws Exception {
        Address address1 = createTestAddress(contact);

        Address address2 = new Address();
        address2.setId(UUID.randomUUID().toString());
        address2.setStreet("Jalan MH Thamrin No. 1");
        address2.setCity("Jakarta Pusat");
        address2.setProvince("DKI Jakarta");
        address2.setCountry("Indonesia");
        address2.setPostalCode("10310");
        address2.setContact(contact);
        addressRepository.save(address2);

        mockMvc.perform(
                get("/api/contacts/" + contact.getId() + "/addresses")
                        .header("X-API-TOKEN", "token-yoga")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<AddressResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {});

            assertNull(response.getErrors());
            assertEquals(2, response.getData().size());

            List<String> addressIds = response.getData().stream()
                    .map(AddressResponse::getId)
                    .toList();
            assertTrue(addressIds.contains(address1.getId()));
            assertTrue(addressIds.contains(address2.getId()));
        });
    }

    // ==================== HELPER ====================

    private Address createTestAddress(Contact contact) {
        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setStreet("Jalan Sudirman No. 45");
        address.setCity("Jakarta Selatan");
        address.setProvince("DKI Jakarta");
        address.setCountry("Indonesia");
        address.setPostalCode("12190");
        address.setContact(contact);
        return addressRepository.save(address);
    }
}