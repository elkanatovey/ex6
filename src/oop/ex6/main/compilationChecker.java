package oop.ex6.main;

import oop.ex6.CompileErrorException;
import oop.ex6.GlobalVariable;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.LinkedList;

public class compilationChecker {

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

    private static int basicLegalCheck(String currentLine, int scope, HashMap<String, GlobalVariable> globals)
            throws CompileErrorException {
        currentLine = currentLine.trim();
        char endOfLine = currentLine.charAt(currentLine.length() - 1);
        switch (endOfLine) {
            case ';':
                if (scope == 0) {
                    regexManager.isValidGlobalVariable(currentLine, globals);
//                    for (GlobalVariable variable : variables) {
//                        if (globals.containsKey(variable.getName()))
//                            throw new CompileErrorException();
//                        globals.put(variable.getName(),variable);
//                    }
                    return 0;
                }
            case '{':
                return 1;
            case '}':
                return -1;
            default: {
                throw new CompileErrorException();
            }
        }
    }
}



//}
