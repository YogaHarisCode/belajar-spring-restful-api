package yogaharis.restful.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;
import yogaharis.restful.entity.User;
import yogaharis.restful.repository.UserRepository;

import java.time.Instant;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(User.class);
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        String token = Optional.ofNullable(request.getHeader("X-API-TOKEN"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        User user = userRepository.findFirstByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (user.getExpiredAt() == null || user.getExpiredAt() < Instant.now().toEpochMilli()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }else {
            return user;
        }
    }
}
