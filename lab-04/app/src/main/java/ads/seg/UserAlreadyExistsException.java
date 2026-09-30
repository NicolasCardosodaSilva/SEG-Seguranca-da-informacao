package ads.seg;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        System.out.println("Usuário ja cadastrado!");
    }
}
