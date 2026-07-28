package yogaharis.restful.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchContactRequest {

    private String name;

    private String email;

    private String phone;

    @NotNull(message = "page cannot null")
    private Integer page;

    @NotNull(message = "size cannot null")
    private Integer size;
}
