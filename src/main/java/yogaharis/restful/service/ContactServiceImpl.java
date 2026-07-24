package yogaharis.restful.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import yogaharis.restful.entity.Contact;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.ContactResponse;
import yogaharis.restful.model.CreateContactRequest;
import yogaharis.restful.repository.ContactRepository;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ContactServiceImpl implements ContactService{

    private ContactRepository contactRepository;

    @Override
    @Transactional
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
}
