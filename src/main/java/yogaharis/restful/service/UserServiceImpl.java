package yogaharis.restful.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.*;
import yogaharis.restful.repository.UserRepository;
import yogaharis.restful.security.BCrypt;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Default implementation of {@link UserService}.
 * <p>
 * Passwords are hashed with {@link BCrypt} before being persisted, and
 * authentication is session-based: a successful login issues a random UUID
 * token with a 30-day expiration, which is later validated on each
 * authenticated request.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    /**
     * Registers a new user account after ensuring the username is not already taken.
     *
     * @param userRequest validated payload containing the desired username, password, and name
     * @return the newly created user as a response DTO
     * @throws ResponseStatusException with {@link HttpStatus#BAD_REQUEST} if the username is already registered
     */
    @Override
    @Transactional
    public UserResponse register(RegisterUserRequest userRequest) {
        // Reject registration if the username is already taken
        if (userRepository.existsById(userRequest.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username already exist");
        }

        User user = new User();
        user.setUsername(userRequest.getUsername());
        // Hash the raw password before it is ever persisted
        user.setPassword(BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt()));
        user.setName(userRequest.getName());

        userRepository.save(user);

        return UserResponse.builder().name(user.getName()).username(user.getUsername()).build();

    }

    /**
     * Authenticates a user by username and password, and issues a new session token
     * valid for 30 days.
     *
     * @param request validated payload containing the username and password
     * @return a token response containing the new token and its expiration timestamp
     * @throws ResponseStatusException with {@link HttpStatus#UNAUTHORIZED} if the username
     *                                 does not exist or the password does not match
     */
    @Override
    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password wrong"));

        // Verify the supplied password against the stored bcrypt hash
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password wrong");
        }

        // Issue a new session token with a 30-day expiration
        user.setToken(UUID.randomUUID().toString());
        user.setExpiredAt(Instant.now().plus(Duration.ofDays(30)).toEpochMilli());

        userRepository.save(user);

        return TokenResponse.builder().token(user.getToken()).expiredAt(user.getExpiredAt()).build();
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @param user the authenticated user, already resolved from the request token
     * @return the user's profile as a response DTO
     */
    @Override
    @Transactional
    public UserResponse get(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    /**
     * Partially updates the profile of the currently authenticated user.
     * <p>
     * Only the name and/or password are updatable, and each is only applied when
     * present and non-blank in the request; all other fields remain unchanged.
     *
     * @param user    the authenticated user, already resolved from the request token
     * @param request validated payload containing the optional fields to update
     * @return the updated user profile as a response DTO
     */
    @Override
    @Transactional
    public UserResponse update(User user, UpdateUserRequest request) {
        // Update only fields that are allowed to be modified
        if (Objects.nonNull(request.getName()) && !request.getName().isBlank()){
            user.setName(request.getName());
        }

        if (Objects.nonNull(request.getPassword()) && !request.getPassword().isBlank()){
            // Re-hash the new password before persisting it
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);

        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    /**
     * Terminates the current session by clearing the user's token and its expiration.
     *
     * @param user the authenticated user, already resolved from the request token
     */
    @Override
    @Transactional
    public void logout(User user) {
        // Invalidate the session by clearing the token and its expiration
        user.setToken(null);
        user.setExpiredAt(null);

        userRepository.save(user);
    }
}