package ecommerce.Apna_Bazaar.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponseDTO {
    private String message;
    private String errorType;
    private LocalDateTime timestamp = LocalDateTime.now();
}
