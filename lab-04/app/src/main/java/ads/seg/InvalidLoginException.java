package ads.seg;

public class InvalidLoginException extends Exception {
    public InvalidLoginException() {
        System.out.println("Login ou senha incorretos!");
    }
}
