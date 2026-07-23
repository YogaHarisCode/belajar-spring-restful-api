package yogaharis.restful.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import yogaharis.restful.model.RegisterUserRequest;
import yogaharis.restful.model.UserResponse;
import yogaharis.restful.model.WebResponse;
import yogaharis.restful.service.UserService;

@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping(
            path = "/api/users",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<UserResponse>> register(@RequestBody RegisterUserRequest userRequest){
        UserResponse resp = userService.register(userRequest);
        WebResponse<UserResponse> webResponse = new WebResponse<>();
        webResponse.setData(resp);
        return ResponseEntity.status(HttpStatus.CREATED).body(webResponse);
    }
}
