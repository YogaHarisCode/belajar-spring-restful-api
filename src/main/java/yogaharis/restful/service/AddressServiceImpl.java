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
import yogaharis.restful.model.UpdateAddressRequest;
import yogaharis.restful.repository.AddressRepository;
import yogaharis.restful.repository.ContactRepository;

import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link AddressService}.
 * <p>
 * Ownership is enforced at the contact level: every operation first resolves
 * the parent contact through {@link ContactRepository#findFirstByUserAndId},
 * which guarantees that a user can never read, modify, or delete an address
 * belonging to a contact they do not own.
 */
@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService{

    private ContactRepository contactRepository;

    private AddressRepository addressRepository;

    /**
     * Converts an {@link Address} entity into its outward-facing response DTO.
     *
     * @param address the persisted address entity
     * @return an {@link AddressResponse} exposing only the fields intended for API consumers
     */
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

    /**
     * Creates a new address under the contact referenced in the request.
     *
     * @param user    the authenticated user who must own the target contact
     * @param request validated payload containing the contact id and address details
     * @return the newly created address as a response DTO
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND} if the contact
     *                                 does not exist or does not belong to the user
     */
    @Override
    @Transactional
    public AddressResponse create(User user, CreateAddressRequest request) {
        // Ensure the target contact exists and belongs to the authenticated user
        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
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

    /**
     * Retrieves a single address belonging to the given contact.
     *
     * @param user      the authenticated user who must own the target contact
     * @param contactId id of the contact the address belongs to
     * @param addressId id of the address to retrieve
     * @return the matching address as a response DTO
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND} if the contact
     *                                 or the address cannot be found for this user
     */
    @Override
    @Transactional(readOnly = true)
    public AddressResponse get(User user, String contactId, String addressId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        return toAddressResponse(address);
    }

    /**
     * Updates an existing address with the values supplied in the request.
     *
     * @param user    the authenticated user who must own the target contact
     * @param request validated payload containing the contact id, address id, and new values
     * @return the updated address as a response DTO
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND} if the contact
     *                                 or the address cannot be found for this user
     */
    @Override
    @Transactional
    public AddressResponse update(User user, UpdateAddressRequest request) {
        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, request.getAddressId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        // Overwrite all editable fields with the values supplied in the request
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        addressRepository.save(address);

        return toAddressResponse(address);
    }

    /**
     * Permanently removes an address from the given contact.
     *
     * @param user      the authenticated user who must own the target contact
     * @param contactId id of the contact the address belongs to
     * @param addressId id of the address to remove
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND} if the contact
     *                                 or the address cannot be found for this user
     */
    @Override
    @Transactional
    public void remove(User user, String contactId, String addressId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        addressRepository.delete(address);
    }

    /**
     * Lists every address belonging to the given contact.
     *
     * @param user      the authenticated user who must own the target contact
     * @param contactId id of the contact whose addresses are being listed
     * @return all addresses associated with the contact, in repository order
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND} if the contact
     *                                 does not exist or does not belong to the user
     */
    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> list(User user, String contactId) {
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        List<Address> addresses = addressRepository.findAllByContact(contact);

        return addresses.stream().map(this::toAddressResponse).toList();
    }
}