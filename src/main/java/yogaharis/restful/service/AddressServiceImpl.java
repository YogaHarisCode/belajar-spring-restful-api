package yogaharis.restful.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import yogaharis.restful.entity.Address;
import yogaharis.restful.entity.Contact;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.AddressResponse;
import yogaharis.restful.model.CreateAddressRequest;
import yogaharis.restful.repository.AddressRepository;
import yogaharis.restful.repository.ContactRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService{

    private ContactRepository contactRepository;

    private AddressRepository addressRepository;

    private AddressResponse toAddressResponse(Address address){
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }

    @Override
    @Transactional
    public AddressResponse create(User user, CreateAddressRequest request) {
        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContact_id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setContact(contact);
        address.setCountry(request.getCountry());
        address.setProvince(request.getProvince());
        address.setPostalCode(request.getPostalCode());

        addressRepository.save(address);

        return toAddressResponse(address);
    }
}
