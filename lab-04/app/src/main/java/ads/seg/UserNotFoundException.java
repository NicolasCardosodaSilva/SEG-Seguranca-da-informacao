package ads.seg;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        System.out.println("Usuário não encontrado");
    }
}
