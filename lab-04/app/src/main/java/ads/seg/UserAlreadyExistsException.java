package ads.seg;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException() {
        System.out.println("Usuário ja cadastrado!");
    }
}
