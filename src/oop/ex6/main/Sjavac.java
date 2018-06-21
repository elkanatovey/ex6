package oop.ex6.main;

import oop.ex6.CompileErrorException;
import java.io.*;

public class Sjavac {

    /*legal number of arguments*/
    private final static int NUM_OF_ARGS = 1;

    /*useful message for common IO errors*/
    private final static String NO_FILE_MSG = "ERROR: No file in source path.\n";

    /* Similar to previous*/
    private final static String BAD_ARGUMENTS_MSG = "ERROR: Wrong usage. Should receive 1 arguments.\n";

    /**
     * The main - this runs the program
     * @param args contains a single string with a file path
     */
    public static void main(String[] args) {
        if (args.length == NUM_OF_ARGS && (new File(args[0])).exists())
        {
            try {
                LineNumberReader fileToCheck = new LineNumberReader(new FileReader(args[0]));
                compilationChecker.compileCheck(fileToCheck);
                System.out.println(0);
                }
             catch (IOException e) {
                 System.out.println(2);
                 System.err.println("Error the following type of IO exception occurred "+ e);
             }
             catch (CompileErrorException e) {
                 System.out.println(1);
             }
        } else {
            if (args.length!= NUM_OF_ARGS) {
                System.out.println(2);
                System.err.println(BAD_ARGUMENTS_MSG);
            }
            else
            System.out.println(2);
                System.err.println(NO_FILE_MSG);
        }
    }
}