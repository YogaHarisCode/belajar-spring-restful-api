package yogaharis.restful.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.RegisterUserRequest;
import yogaharis.restful.model.UserResponse;
import yogaharis.restful.repository.UserRepository;
import yogaharis.restful.security.BCrypt;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public UserResponse register(RegisterUserRequest userRequest) {
        if (userRepository.existsById(userRequest.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username already exist");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt()));
        user.setName(userRequest.getName());

        userRepository.save(user);

        return UserResponse.builder().name(user.getName()).username(user.getUsername()).build();

    }
}
