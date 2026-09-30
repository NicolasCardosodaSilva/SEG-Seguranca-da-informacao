package ads.seg;

public class UserNotFoundException extends Exception {
    public UserNotFoundException() {
        System.out.println("Usuário não encontrado");
    }
}
