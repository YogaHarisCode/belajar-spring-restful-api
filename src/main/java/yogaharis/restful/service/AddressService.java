package yogaharis.restful.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import yogaharis.restful.entity.Address;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.AddressResponse;
import yogaharis.restful.model.CreateAddressRequest;
import yogaharis.restful.model.UpdateAddressRequest;

import java.util.List;

/**
 * Defines the contract for managing {@link Address} records that belong to a
 * contact owned by the authenticated {@link User}.
 * <p>
 * Every operation exposed by this service is implicitly scoped to a specific
 * contact, and implementations are expected to verify that the contact
 * belongs to the requesting user before any address is created, read,
 * updated, deleted, or listed.
 */
@Validated
public interface AddressService {

    /**
     * Creates a new address for the contact referenced in the request.
     *
     * @param user    the authenticated user who owns the target contact
     * @param request validated payload containing the contact id and address details
     * @return the created address as a response DTO
     */
    AddressResponse create(User user, @Valid CreateAddressRequest request);

    /**
     * Retrieves a single address belonging to the given contact.
     *
     * @param user      the authenticated user who owns the target contact
     * @param contactId id of the contact the address belongs to
     * @param addressId id of the address to retrieve
     * @return the matching address as a response DTO
     */
    AddressResponse get(User user, @NotBlank(message = "{contactId.notBlank}") String contactId, @NotBlank(message = "{addressId.notBlank}") String addressId);

    /**
     * Updates an existing address with the values supplied in the request.
     *
     * @param user    the authenticated user who owns the target contact
     * @param request validated payload containing the contact id, address id, and new values
     * @return the updated address as a response DTO
     */
    AddressResponse update(User user, @Valid UpdateAddressRequest request);

    /**
     * Permanently removes an address from the given contact.
     *
     * @param user      the authenticated user who owns the target contact
     * @param contactId id of the contact the address belongs to
     * @param addressId id of the address to remove
     */
    void remove(User user, @NotBlank(message = "{contactId.notBlank}") String contactId, @NotBlank(message = "{addressId.notBlank}") String addressId);

    /**
     * Lists every address belonging to the given contact.
     *
     * @param user      the authenticated user who owns the target contact
     * @param contactId id of the contact whose addresses are being listed
     * @return all addresses associated with the contact
     */
    List<AddressResponse> list(User user, @NotBlank(message = "{contactId.notBlank}") String contactId);
}