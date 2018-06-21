package oop.ex6;

public class CompileErrorException extends Exception{

    private static String MESSAGE = "Error: compilation error";

    private String message;

    public CompileErrorException(){
        message = MESSAGE;
    }



    public String getMessage() {
        return message;
    }
}
