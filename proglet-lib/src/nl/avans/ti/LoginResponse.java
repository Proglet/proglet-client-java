package nl.avans.ti;

public class LoginResponse {
    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                '}';
    }

    private final String token;

    public LoginResponse(String token) {
        this.token = token;
    }
}
