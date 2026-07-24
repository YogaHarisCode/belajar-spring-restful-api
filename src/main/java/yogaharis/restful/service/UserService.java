package yogaharis.restful.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.LoginUserRequest;
import yogaharis.restful.model.RegisterUserRequest;
import yogaharis.restful.model.TokenResponse;
import yogaharis.restful.model.UserResponse;

@Validated
public interface UserService {

    UserResponse register(@Valid RegisterUserRequest userRequest);

    TokenResponse login(@Valid LoginUserRequest request);

    UserResponse get(User user);
}
