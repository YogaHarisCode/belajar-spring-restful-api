package yogaharis.restful.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.AddressResponse;
import yogaharis.restful.model.CreateAddressRequest;

@Validated
public interface AddressService {

    AddressResponse create(User user, @Valid CreateAddressRequest request);
}
