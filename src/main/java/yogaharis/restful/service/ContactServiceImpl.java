package yogaharis.restful.service;

import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import yogaharis.restful.entity.Contact;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.ContactResponse;
import yogaharis.restful.model.CreateContactRequest;
import yogaharis.restful.model.SearchContactRequest;
import yogaharis.restful.model.UpdateContactRequest;
import yogaharis.restful.repository.ContactRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Default implementation of {@link ContactService}.
 * <p>
 * Ownership is enforced by always scoping lookups to the authenticated
 * {@link User}, either through {@link ContactRepository#findFirstByUserAndId}
 * for single-contact operations or through a dynamic {@link Specification}
 * for search queries.
 */
@Service
@AllArgsConstructor
public class ContactServiceImpl implements ContactService {

    private ContactRepository contactRepository;

    /**
     * Creates a new contact owned by the authenticated user.
     *
     * @param user    the authenticated user who will own the new contact
     * @param request validated payload containing the contact details
     * @return the created contact as a response DTO
     */
    @Transactional
    @Override
    public ContactResponse create(User user, CreateContactRequest request) {
        Contact contact = new Contact();
        contact.setId(UUID.randomUUID().toString());
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setUser(user);
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());

        contactRepository.save(contact);

        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }

    /**
     * Retrieves a single contact owned by the authenticated user.
     *
     * @param user the authenticated user who must own the contact
     * @param id   id of the contact to retrieve
     * @return the matching contact as a response DTO
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND} if the contact
     *                                 does not exist or does not belong to the user
     */
    @Transactional(readOnly = true)
    @Override
    public ContactResponse get(User user, String id) {
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }

    /**
     * Updates an existing contact with the values supplied in the request.
     *
     * @param user    the authenticated user who must own the contact
     * @param request validated payload containing the contact id and new values
     * @return the updated contact as a response DTO
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND} if the contact
     *                                 does not exist or does not belong to the user
     */
    @Transactional
    @Override
    public ContactResponse update(User user, UpdateContactRequest request) {
        Contact contact = contactRepository.findFirstByUserAndId(user, request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contactRepository.save(contact);

        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }

    /**
     * Permanently deletes a contact owned by the authenticated user.
     *
     * @param user the authenticated user who must own the contact
     * @param id   id of the contact to delete
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND} if the contact
     *                                 does not exist or does not belong to the user
     */
    @Transactional
    @Override
    public void delete(User user, String id) {
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contactRepository.delete(contact);
    }

    /**
     * Searches the authenticated user's contacts using optional filters, returning
     * a paginated result.
     * <p>
     * Filtering is performed with a dynamic {@link Specification} so that only the
     * criteria present in the request are applied; unset filters are simply omitted
     * from the query.
     *
     * @param user    the authenticated user whose contacts are being searched
     * @param request validated payload containing optional name/email/phone filters
     *                and pagination parameters
     * @return a page of contacts matching the supplied filters
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ContactResponse> search(User user, SearchContactRequest request) {
        // Build the search specification dynamically based on the filters provided
        Specification<Contact> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Always restrict results to contacts owned by the requesting user
            predicates.add(criteriaBuilder.equal(root.get("user"), user));

            // Match name filter against both first name and last name
            if (Objects.nonNull(request.getName())){
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("firstName"), "%" + request.getName() + "%"),
                        criteriaBuilder.like(root.get("lastName"), "%" + request.getName() + "%")
                ));
            }

            if (Objects.nonNull(request.getEmail())){
                predicates.add(criteriaBuilder.like(root.get("email"), "%"+request.getEmail()+"%"));
            }

            if ((Objects.nonNull(request.getPhone()))){
                predicates.add(criteriaBuilder.like(root.get("phone"), "%"+request.getPhone()+"%"));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        // Execute the specification with pagination and map entities to response DTOs
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Contact> page = contactRepository.findAll(specification, pageable);
        List<ContactResponse> contactResponse = page.getContent().stream()
                .map(contact -> ContactResponse.builder()
                        .firstName(contact.getFirstName())
                        .lastName(contact.getLastName())
                        .id(contact.getId())
                        .phone(contact.getPhone())
                        .email(contact.getEmail())
                        .build())
                .toList();

        return new PageImpl<>(contactResponse, pageable, page.getTotalElements());
    }
}