package gpExceptions;

public final class UnsupportedUserTypeException extends Exception {
    public String userType;

    public UnsupportedUserTypeException(String userType) {
        this.userType = userType;
    }
}