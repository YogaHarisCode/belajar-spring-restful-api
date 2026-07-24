package yogaharis.restful.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.LoginUserRequest;
import yogaharis.restful.model.RegisterUserRequest;
import yogaharis.restful.model.TokenResponse;
import yogaharis.restful.model.UserResponse;
import yogaharis.restful.repository.UserRepository;
import yogaharis.restful.security.BCrypt;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    @Transactional
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

    @Override
    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password wrong"));

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password wrong");
        }

        user.setToken(UUID.randomUUID().toString());
        user.setExpiredAt(Instant.now().plus(Duration.ofDays(30)).toEpochMilli());

        userRepository.save(user);

        return TokenResponse.builder().token(user.getToken()).expiredAt(user.getExpiredAt()).build();
    }
}
