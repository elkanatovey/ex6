package oop.ex6.main;

import oop.ex6.CompileErrorException;
import oop.ex6.dataStructures.GlobalVariable;
import oop.ex6.dataStructures.LocalVariable;
import oop.ex6.dataStructures.Method;

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
        LinkedList<Method> methods = new LinkedList<>();
        String currentLine;
        int scope = 0, previousScope = 0;
        do {
            currentLine = fileToRead.readLine();
            if (regexManager.isLineIgnorable(currentLine))  // in case of a whitespace line, or comment line
                continue;
            scope = scope + basicLegalCheck(currentLine, scope, globals, methods);
            if (scope!= GLOBAL_SCOPE){
                    methods.peekLast().addLine(currentLine.trim());
            }
            else if (previousScope!= GLOBAL_SCOPE){
                methods.peekLast().addLine(currentLine.trim());
            }
            previousScope = scope;
        } while (currentLine != null);
        checkMethods(globals, methods);
    }

    /*
    check type of current line
     */
    private static int basicLegalCheck(String currentLine, int scope, HashMap<String, GlobalVariable>
            globals, LinkedList<Method> methods)
            throws CompileErrorException {
        currentLine = currentLine.trim();
        char endOfLine = currentLine.charAt(currentLine.length() - 1);
        switch (endOfLine) {
            case ';':
                if (scope == GLOBAL_SCOPE) {
                    regexManager.isValidGlobalVariable(currentLine, globals);
                }
                return 0;
            case '{':
                if (scope==GLOBAL_SCOPE) {
                    HashMap<String, LocalVariable> currentLocals = new HashMap<>();
                    regexManager.isValidParameterVariable(currentLine, methods,currentLocals);
                }
                else
                    regexManager.isIfWhileStatement(currentLine);
                return 1;
            case '}':
                if (scope<=GLOBAL_SCOPE)
                    throw new CompileErrorException();
                return -1;
            default: {
                throw new CompileErrorException();
            }
        }
    }



    /*
    Check if the current method can legally compile
     */
    private static void checkMethods(HashMap<String, GlobalVariable> globals, LinkedList<Method> methods)
            throws CompileErrorException{
        for (Method currentMethod: methods){
            currentMethod.getLinesToRead().pollFirst(); // removes header line
            if (currentMethod.getLinesToRead().size()<2) //todo remove first line from method lines
                throw new CompileErrorException();
            if (currentMethod.isLegalMethodClose())
                currentMethod.checkLegal(globals);
            else
                throw new CompileErrorException();
        }

    }
}



//}
