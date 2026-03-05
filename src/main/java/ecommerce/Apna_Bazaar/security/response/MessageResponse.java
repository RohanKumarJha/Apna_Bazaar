package ecommerce.Apna_Bazaar.security.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.SecondaryRow;

@AllArgsConstructor
@Getter
@SecondaryRow
public class MessageResponse {
    private String message;
}
