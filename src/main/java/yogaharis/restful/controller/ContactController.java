package yogaharis.restful.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.*;
import yogaharis.restful.service.ContactService;

import java.util.List;

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

    @GetMapping(
            path = "/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> get(User user, @NotBlank(message = "id cannot blank") @PathVariable(name = "contactId") String id){
        ContactResponse contactResponse = contactService.get(user, id);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }

    @PutMapping(
            path = "/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> update(User user,
                                               @RequestBody UpdateContactRequest request,
                                               @NotBlank(message = "id cannot null") @PathVariable(name = "contactId") String id){
        request.setId(id);
        ContactResponse update = contactService.update(user, request);
        return WebResponse.<ContactResponse>builder().data(update).build();
    }

    @DeleteMapping(
            path = "/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user,
                                      @NotBlank(message = "id cannot blank") @PathVariable(name = "contactId") String id){
        contactService.delete(user, id);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ContactResponse>> search(User user,
                                                     @RequestParam(name = "name", required = false) String name,
                                                     @RequestParam(name = "email", required = false) String email,
                                                     @RequestParam(name = "phone", required = false) String phone,
                                                     @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                                     @RequestParam(name = "size", required = false, defaultValue = "10") Integer size){
        SearchContactRequest request = SearchContactRequest.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .page(page)
                .size(size)
                .build();

        Page<ContactResponse> contactResponses = contactService.search(user, request);

        PagingResponse paging = PagingResponse.builder()
                .currentPage(contactResponses.getNumber())
                .size(contactResponses.getSize())
                .totalPage(contactResponses.getTotalPages())
                .build();

        return WebResponse.<List<ContactResponse>>builder().data(contactResponses.getContent()).pagingResponse(paging).build();
    }
}
