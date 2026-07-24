package yogaharis.restful.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserRequest {

    @NotBlank(message = "{username.notblank}")
    @Size(
            message = "{username.size}",
            max = 100
    )
    private String username;

    @NotBlank(message = "{password.notblank}")
    @Size(
            message = "{password.size}",
            max = 100
    )
    private String password;
}
