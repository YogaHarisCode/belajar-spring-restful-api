package yogaharis.restful.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    @Size(
            message = "{name.size}",
            max = 100
    )
    private String name;

    @Size(
            message = "{password.size}",
            max = 100
    )
    private String password;
}
