package ads.seg;

public class InvalidPasswordException extends Exception {
    public InvalidPasswordException() {
        System.out.println("Senha não corresponde");
    }
}
