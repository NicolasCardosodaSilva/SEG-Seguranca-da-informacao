package ads.seg;

public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException() {
        System.out.println("Login ou senha incorretos!");
    }
}
