package ecommerce.Apna_Bazaar.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {

    @NotBlank(message = "CategoryName should not be blank")
    @Size(max = 50)
    private String name;

    @NotBlank(message = "Category description should not be blank")
    private String description;
}
