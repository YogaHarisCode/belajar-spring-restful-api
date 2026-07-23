package yogaharis.restful.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import yogaharis.restful.model.RegisterUserRequest;
import yogaharis.restful.model.UserResponse;

@Validated
public interface UserService {

    @Valid
    UserResponse register(@Valid RegisterUserRequest userRequest);
}
