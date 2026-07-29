package yogaharis.restful.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.ContactResponse;
import yogaharis.restful.model.CreateContactRequest;
import yogaharis.restful.model.SearchContactRequest;
import yogaharis.restful.model.UpdateContactRequest;

/**
 * Defines the contract for managing contacts owned by an authenticated
 * {@link User}.
 * <p>
 * All operations are implicitly scoped to the requesting user; a user can
 * only create, read, update, delete, or search their own contacts.
 */
@Validated
public interface ContactService {

    /**
     * Creates a new contact owned by the authenticated user.
     *
     * @param user    the authenticated user who will own the new contact
     * @param request validated payload containing the contact details
     * @return the created contact as a response DTO
     */
    ContactResponse create(User user, @Valid CreateContactRequest request);

    /**
     * Retrieves a single contact owned by the authenticated user.
     *
     * @param user the authenticated user who must own the contact
     * @param id   id of the contact to retrieve
     * @return the matching contact as a response DTO
     */
    ContactResponse get(User user, @NotBlank(message = "id cannot blank") String id);

    /**
     * Updates an existing contact with the values supplied in the request.
     *
     * @param user    the authenticated user who must own the contact
     * @param request validated payload containing the contact id and new values
     * @return the updated contact as a response DTO
     */
    ContactResponse update(User user, @Valid UpdateContactRequest request);

    /**
     * Permanently deletes a contact owned by the authenticated user.
     *
     * @param user the authenticated user who must own the contact
     * @param id   id of the contact to delete
     */
    void delete(User user, @NotBlank(message = "id cannot blank") String id);

    /**
     * Searches the authenticated user's contacts using optional filters, returning
     * a paginated result.
     *
     * @param user    the authenticated user whose contacts are being searched
     * @param request validated payload containing optional name/email/phone filters
     *                and pagination parameters
     * @return a page of contacts matching the supplied filters
     */
    Page<ContactResponse> search(User user, @Valid SearchContactRequest request);
}