package gpExceptions;

public final class UnknownUserException extends Exception {
    public int userID;

    public UnknownUserException(int userID) {
        this.userID = userID;
    }
}