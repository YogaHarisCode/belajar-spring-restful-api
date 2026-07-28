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

@Service
@AllArgsConstructor
public class ContactServiceImpl implements ContactService {

    private ContactRepository contactRepository;

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

    @Transactional
    @Override
    public void delete(User user, String id) {
        Contact contact = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contactRepository.delete(contact);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactResponse> search(User user, SearchContactRequest request) {
        Specification<Contact> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("user"), user));

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
