package yogaharis.restful.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.*;

@Validated
public interface UserService {

    UserResponse register(@Valid RegisterUserRequest userRequest);

    TokenResponse login(@Valid LoginUserRequest request);

    UserResponse get(User user);

    UserResponse update(User user, @Valid UpdateUserRequest request);
}
