package ads.seg;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        System.out.println("Senha não corresponde");
    }
}
