package yogaharis.restful.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.*;
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

    @PostMapping(
            path = "/api/users/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request){
        return WebResponse.<TokenResponse>builder().data(userService.login(request))
                .build();
    }

    @GetMapping(
            path = "/api/users/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> get(User user){
        UserResponse userResponse = userService.get(user);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PatchMapping(
            path = "/api/users/current",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest request){
        return WebResponse.<UserResponse>builder()
                .data(userService.update(user, request))
                .build();
    }

    @DeleteMapping(
            path = "/api/users/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> logout(User user){
        userService.logout(user);
        return WebResponse.<String>builder().data("OK").build();
    }
}
