package yogaharis.restful.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.ContactResponse;
import yogaharis.restful.model.CreateContactRequest;
import yogaharis.restful.model.UpdateContactRequest;

@Validated
public interface ContactService {

    ContactResponse create(User user, @Valid CreateContactRequest request);

    ContactResponse get(User user, @NotBlank(message = "id cannot blank") String id);

    ContactResponse update(User user, @Valid UpdateContactRequest request);

    void delete(User user, @NotBlank(message = "id cannot blank") String id);
}
