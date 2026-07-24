package yogaharis.restful.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import yogaharis.restful.resolver.UserArgumentResolver;

import java.util.List;

@Configuration
@AllArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

    private UserArgumentResolver userArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }
}
