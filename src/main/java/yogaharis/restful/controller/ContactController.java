package yogaharis.restful.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.ContactResponse;
import yogaharis.restful.model.CreateContactRequest;
import yogaharis.restful.model.WebResponse;
import yogaharis.restful.service.ContactService;

@RestController
@Validated
@AllArgsConstructor
public class ContactController {

    private ContactService contactService;

    @PostMapping(
            path = "/api/contacts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> create(User user, @Valid @RequestBody CreateContactRequest request){
        ContactResponse contactResponse = contactService.create(user, request);

        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }
}
