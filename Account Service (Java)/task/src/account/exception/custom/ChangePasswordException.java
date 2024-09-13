package account.exception.custom;

public class ChangePasswordException extends RuntimeException{
    public ChangePasswordException(String message) {
        super(message);
    }
}
