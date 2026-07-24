package yogaharis.restful.model;

import jakarta.validation.constraints.Email;
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
public class CreateContactRequest {

    @NotBlank(
            message = "{firstName.notblank}"
    )
    @Size(
            message = "{firstName.size}",
            max = 100
    )
    private String firstName;

    @Size(
            message = "{lastName.size}",
            max = 100
    )
    private String lastName;

    @Size(
            message = "{email.size}",
            max = 100
    )
    @Email(message = "{email.format}")
    private String email;


    @Size(
            message = "{phone.size}",
            max = 100
    )
    private String phone;
}
