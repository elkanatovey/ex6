package oop.ex6;

public class illegalAssignmentException extends CompileErrorException {

    private static String MESSAGE = "Error: illegal variable assignment";

    String message;

    public illegalAssignmentException(){
        message = MESSAGE;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
