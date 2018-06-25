package oop.ex6;

/**
 * an exception to be thrown if the inputted file is an illegal sjava program
 */
public class CompileErrorException extends Exception{

    private static String MESSAGE = "Error: compilation error";

    private String message;

    public CompileErrorException(){
        printStackTrace();
        message = MESSAGE;
    }



    public String getMessage() {
        return message;
    }
}
