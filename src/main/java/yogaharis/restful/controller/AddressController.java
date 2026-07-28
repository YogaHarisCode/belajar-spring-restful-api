package yogaharis.restful.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.AddressResponse;
import yogaharis.restful.model.CreateAddressRequest;
import yogaharis.restful.model.UpdateAddressRequest;
import yogaharis.restful.model.WebResponse;
import yogaharis.restful.service.AddressService;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class AddressController {

    private AddressService addressService;

    @PostMapping(
            path = "/api/contacts/{contactId}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> create(
            User user,
            @RequestBody CreateAddressRequest request,
            @PathVariable(name = "contactId") String contactId
    ){
        request.setContactId(contactId);

        AddressResponse addressResponse = addressService.create(user, request);

        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @GetMapping(
            path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> get(
            User user,
            @NotBlank(message = "{contactId.notBlank}") @PathVariable(name = "contactId") String contactId,
            @NotBlank(message = "{addressId.notBlank}") @PathVariable(name = "addressId") String addressId
    ){
        AddressResponse addressResponse = addressService.get(user, contactId, addressId);

        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @PutMapping(
            path = "/api/contacts/{contactId}/addresses/{addressId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> update(
            User user,
            @RequestBody UpdateAddressRequest request,
            @NotBlank(message = "{contactId.notBlank}") @PathVariable(name = "contactId") String contactId,
            @NotBlank(message = "{addressId.notBlank}") @PathVariable(name = "addressId") String addressId
    ){
        request.setContactId(contactId);
        request.setAddressId(addressId);
        AddressResponse addressResponse = addressService.update(user, request);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @DeleteMapping(
            path = "/api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> remove(
            User user,
            @NotBlank(message = "{contactId.notBlank}") @PathVariable(name = "contactId") String contactId,
            @NotBlank(message = "{addressId.notBlank}") @PathVariable(name = "addressId") String addressId
    ){
        addressService.remove(user, contactId, addressId);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/api/contacts/{contactId}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AddressResponse>> list(
            User user,
            @NotBlank(message = "{contactId.notBlank}") @PathVariable(name = "contactId") String contactId
    ){
        List<AddressResponse> addressResponses = addressService.list(user, contactId);
        return WebResponse.<List<AddressResponse>>builder().data(addressResponses).build();
    }
}
