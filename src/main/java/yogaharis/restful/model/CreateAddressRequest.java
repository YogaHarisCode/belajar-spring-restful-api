package yogaharis.restful.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CreateAddressRequest {

    @JsonIgnore
    @NotBlank(message = "{contactId.notBlank}")
    private String contactId;

    @Size(max = 200, message = "{street.size}")
    private String street;

    @Size(max = 100, message = "{city.size}")
    private String city;

    @Size(max = 100, message = "{province.size}")
    private String province;

    @NotBlank(message = "{country.notBlank}")
    @Size(max = 100, message = "{country.size}")
    private String country;

    @Size(max = 10, message = "{postalCode.size}")
    private String postalCode;


}
