package yogaharis.restful.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.AddressResponse;
import yogaharis.restful.model.CreateAddressRequest;

@Validated
public interface AddressService {

    AddressResponse create(User user, @Valid CreateAddressRequest request);

    AddressResponse get(User user, @NotBlank(message = "contact id cannot blank") String contactId, @NotBlank(message = "address id cannot blank") String addressId);
}
