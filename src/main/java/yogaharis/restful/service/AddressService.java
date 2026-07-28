package yogaharis.restful.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.AddressResponse;
import yogaharis.restful.model.CreateAddressRequest;
import yogaharis.restful.model.UpdateAddressRequest;

@Validated
public interface AddressService {

    AddressResponse create(User user, @Valid CreateAddressRequest request);

    AddressResponse get(User user, @NotBlank(message = "{contactId.notBlank}") String contactId, @NotBlank(message = "{addressId.notBlank}") String addressId);

    AddressResponse update(User user, @Valid UpdateAddressRequest request);

    void remove(User user, @NotBlank(message = "{contactId.notBlank}") String contactId, @NotBlank(message = "{addressId.notBlank}") String addressId);
}
