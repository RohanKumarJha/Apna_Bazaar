package ecommerce.Apna_Bazaar.security.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String username;
    private List<String> roles;
    private String jwtToken;
}
