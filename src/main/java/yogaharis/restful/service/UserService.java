package yogaharis.restful.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import yogaharis.restful.entity.User;
import yogaharis.restful.model.*;

/**
 * Defines the contract for user account management, including registration,
 * authentication, profile access, profile updates, and session termination.
 */
@Validated
public interface UserService {

    /**
     * Registers a new user account.
     *
     * @param userRequest validated payload containing the desired username, password, and name
     * @return the newly created user as a response DTO
     */
    UserResponse register(@Valid RegisterUserRequest userRequest);

    /**
     * Authenticates a user and issues a new session token.
     *
     * @param request validated payload containing the username and password
     * @return a token response containing the new token and its expiration
     */
    TokenResponse login(@Valid LoginUserRequest request);

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @param user the authenticated user
     * @return the user's profile as a response DTO
     */
    UserResponse get(User user);

    /**
     * Partially updates the profile of the currently authenticated user.
     * <p>
     * Only the fields present in the request are applied; omitted or blank
     * fields are left unchanged.
     *
     * @param user    the authenticated user
     * @param request validated payload containing the optional fields to update
     * @return the updated user profile as a response DTO
     */
    UserResponse update(User user, @Valid UpdateUserRequest request);

    /**
     * Terminates the current session by invalidating the user's token.
     *
     * @param user the authenticated user
     */
    void logout(User user);
}