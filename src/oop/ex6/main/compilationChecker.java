package oop.ex6.main;

import oop.ex6.CompileErrorException;
import oop.ex6.GlobalVariable;
import oop.ex6.Method;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.LinkedList;

public class compilationChecker {


    private static final int GLOBAL_SCOPE = 0;
    /**
     * receive a file to read and check if it compiles
     * @param fileToRead
     * @throws CompileErrorException
     * @throws IOException
     */
    public static void compileCheck(LineNumberReader fileToRead) throws CompileErrorException, IOException {
        HashMap<String,GlobalVariable> globals = new HashMap<>();
        String currentLine;
        int scope = 0;
        do {
            currentLine = fileToRead.readLine();
            if (regexManager.isLineIgnorable(currentLine))  // in case of a whitespace line, or comment line
                continue;
            scope = scope + basicLegalCheck(currentLine, scope, globals);


        } while (currentLine != null);
    }

    /*
    check type of current line
     */
    private static int basicLegalCheck(String currentLine, int scope, HashMap<String, GlobalVariable> globals)
            throws CompileErrorException {
        currentLine = currentLine.trim();
        char endOfLine = currentLine.charAt(currentLine.length() - 1);
        switch (endOfLine) {
            case ';':
                if (scope == GLOBAL_SCOPE) {
                    regexManager.isValidGlobalVariable(currentLine, globals);
                    return 0;
                }
                regexManager.
            case '{':
                if (scope==GLOBAL_SCOPE) {
                    regexManager.isValidParameterVariable(currentLine,);
                    regexManager.
                }
                regexManager.isIfStatement(currentLine);
                regexManager.isWhileStatement(currentLine);
                return 1;
            case '}':
                if (scope==GLOBAL_SCOPE+1)
                    //check for return
                return -1;
            default: {
                throw new CompileErrorException();
            }
        }
    }

    private static void checkMethods(HashMap<String, GlobalVariable>, HashMap<String,Method>){

    }
}



//}
