package samdi.demo.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import samdi.demo.DTO.TokenRequest;
import samdi.demo.JWT.JwtProvider;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtProvider provider;

    public AuthController(JwtProvider provider) {
        this.provider = provider;
    }

    @PostMapping("/login")
    public String login() {
        String token = provider.createToken("adsf", "role");
        return token;
    }

    @PostMapping("/verify")
    public boolean login(@RequestBody TokenRequest token) {
        boolean result = provider.validateToken(token.token());
        return result;
    }
}
