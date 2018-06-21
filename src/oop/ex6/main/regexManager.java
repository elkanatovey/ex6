package oop.ex6.main;

import oop.ex6.CompileErrorException;
import oop.ex6.GlobalVariable;
import oop.ex6.Method;
import oop.ex6.Variable;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.*;

public class regexManager {

    private static final String INT = "int", DOUBLE = "double", STRING = "String", BOOLEAN = "boolean", CHAR = "char";
    //    private static final String ONLY_SPACES_IN_LINE = "^\\s*$";
//    private static final String STARTS_WITH_BACKSLASH = "^\\/\\/.*";
//    private static final String END_OF_BLOCK = "(}\\s*$)";
//    private static final String START_OF_BLOCK = "({\\s*$)";
//    private static final String ENDS_WITH_COMMA = ";\\s*$";
    private static final String FINAL_STATEMENT = ("\\s*(final\\s+.*)");
    private static final Pattern SPACE_PATTERN = Pattern.compile("^\\s*$");
    private static final Pattern BACKSLASH_PATTERN = Pattern.compile("^\\/\\/.*");
    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("(\\s*\\D\\w*?_*\\s*)|(_\\w+)");
//    private static final Pattern METHOD_NAME_PATTERN = Pattern.compile("(\\s*[A-Za-z]\\w*\\w*\\s*)|(_\\w+)");
//    private static final String METHOD_PARAMETERS = "(\\(\\))";
    private static final String METHOD_NAME = "(\\s*[A-Za-z]\\w*\\s*)|(_\\w+\\s*)";
    private static final Pattern METHOD_PATTERN = Pattern.compile("\\s*void\\s*(([A-Za-z]\\w*\\s*)|([_]\\w+\\s*))\\s*[(](.*)[)]\\s*[{]");
//    private static final Pattern PARAMETERS_PATTERN = Pattern.compile("\\s*\\(\\)\\s*");
//    private static final Pattern RETURN_STATEMENT_PATTERN = Pattern.compile("\\s*(return\\s*;)$");
//    private static final Pattern OPEN_STATEMENT_PATTERN = Pattern.compile("\\s*(\\{\\s*)$");
    private static final Pattern CLOSED_STATEMENT_PATTERN = Pattern.compile("\\s*(}\\s*)");
    private static final Pattern IF_PATTERN = Pattern.compile("\\s*if\\s*[(](.*)[)]\\s*[{]");
    private static final Pattern WHILE_PATTERN = Pattern.compile("\\s*while\\s*[(](.*)[)]\\s*[{]");
    private static final Pattern FINAL_STATEMENT_PATTERN
            = Pattern.compile("\\s*(final\\s*)\\s+((int|double|boolean|char|String)\\s+.*)$");
    //check if before final there spaces
    private static final Pattern TYPE_PATTERN = Pattern.compile("\\s*((int|double|boolean|char|String)\\s+).*");
    private static final Pattern FINAL_PATTERN = Pattern.compile(FINAL_STATEMENT);
    private static final Pattern COMMA_PATTERN = Pattern.compile(".*,\\s*(,).*");
    private static final String RESERVED_KEYWORDS =
            "((\\w*\\s+)|\\s*)((int|double|boolean|char|String|void|final|if|while|true|false|return)\\s+.*)";
    private static final Pattern RESERVED_KEYWORDS_PATTERN = Pattern.compile(RESERVED_KEYWORDS);
    private static final Pattern INT_PATTERN = Pattern.compile("(\\s*\\d+\\s*)|(\\s*-\\d+\\s*)");
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[+'-]?\\d*\\.?\\d+");
    private static final Pattern STRING_PATTERN = Pattern.compile("\\s*\".*(.*)+\\s*.*\"\\s*"); //how to add ""
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile(".\\s+(true|false)\\s+.");
    private static final Pattern EQULE_COMMA_PATTERN = Pattern.compile("\\w*((=)|,)\\s*");
    private static final Pattern CHAR_PATTERN = Pattern.compile("'.'"); //no spaces inside
    private static final Pattern VAR_TO_VAR_PATTERN = Pattern.compile("(\\s*\\D\\w*\\s*=\\D\\w*)|(\\s*\\D\\w*\\s*=\\d\\s*)"); //a=b or a=5 without type


    /**
     * This function checks if a line is ignorable, and should be passed over
     *
     * @param currentLine the line to read
     * @return true/ false based upon if the line is ignorable
     */
    public static boolean isLineIgnorable(String currentLine) {
        Matcher matcher1 = SPACE_PATTERN.matcher(currentLine);
        Matcher matcher2 = BACKSLASH_PATTERN.matcher(currentLine);
        if (matcher1.matches() || matcher2.matches())
            return true;
        else
            return false;
    }

    /**
     * @param lineToRead
     * @return
     * @throws CompileErrorException
     */
    public static char endOfLineType(String lineToRead) throws CompileErrorException {
        lineToRead = lineToRead.trim();
        char endOfLine = lineToRead.charAt(lineToRead.length() - 1);
        if ((endOfLine == '{') || (endOfLine == '}') || (endOfLine == ';'))
            return endOfLine;
        throw new CompileErrorException();// if we have reached this line, the file will not compile
    }

    /**
     * @param lineToRead
     * @return
     * @throws CompileErrorException
     */
    public static void isValidGlobalVariable
    (String lineToRead, HashMap<String, GlobalVariable> globalHashMap) throws CompileErrorException {
        lineToRead = lineToRead.trim();
        if (lineToRead.endsWith(";"))
            lineToRead = lineToRead.substring(0, lineToRead.length() - 1);
        boolean isFinal = finalHandle(lineToRead);
        if (isFinal)
            lineToRead = lineToRead.replaceFirst("final", "");
        Matcher validVariableWithNoTypeMatcher = VAR_TO_VAR_PATTERN.matcher(lineToRead);
        //pattern to match a specific case a=b without type
        if (validVariableWithNoTypeMatcher.matches()){
            if(AssignVariableToVariable(lineToRead,globalHashMap))
                return;
            //todo check
        }
        String typeTuple[] = typeChecker(lineToRead);
        String typeToInsert = typeTuple[0], variables = typeTuple[1];

        String[] allVariables = illegalCommaChecker(variables);
        for (String variableToAnalyze : allVariables) {
            String[] specificVariable = variableToAnalyze.split("=");
            int allowedLength = 2;
            if (specificVariable.length > allowedLength || (isFinal && specificVariable.length == 1))
                throw new CompileErrorException();
            specificVariable[0] = specificVariable[0].trim();
            Matcher validVariableMatcher = VARIABLE_NAME_PATTERN.matcher(specificVariable[0]);
            if (validVariableMatcher.matches()) {
                Matcher reservedKeyMatches = RESERVED_KEYWORDS_PATTERN.matcher(specificVariable[0]); // check that the variable isn't a reserved word
                if (reservedKeyMatches.matches()) {
                    throw new CompileErrorException();  // illegal variable name
                }
                boolean initialization = false;
                if (specificVariable.length == 2) {
                    initialization = true;
                    if (!checkLegalAssignment(specificVariable[1], typeToInsert, globalHashMap)) //check the the type is legal
                        throw new CompileErrorException();
                } else if (isFinal)
                    throw new CompileErrorException();
                if (globalHashMap.containsKey(specificVariable[0])) {
                    GlobalVariable existVar = globalHashMap.get(specificVariable[0]);
                    if (existVar.isInitialization())
                        throw new CompileErrorException();
                }
                //if the key exs but the existing global variable isn't initialize it ok
                globalHashMap.put(specificVariable[0], new GlobalVariable(typeToInsert, initialization, isFinal, specificVariable[0]));
                //todo specificVariable[0] twice ? the first supposed to be type
            } else
                throw new CompileErrorException();
        }
//        return globals;
    }


    /**
     * checks if a type value pairing is legal
     *
     * @param AssignmentToCheck
     * @param type
     * @return
     */
    private static boolean checkLegalAssignment
    (String AssignmentToCheck, String type, HashMap<String, GlobalVariable> globals) throws CompileErrorException {
        AssignmentToCheck = AssignmentToCheck.trim();
        HashMap<String, Variable> a = (HashMap) globals;  //todo be fixed
        if (checkLegalAssignmentHelper(AssignmentToCheck, type, a)) {
            return true;
        }
        switch (type) {
            case INT: {
                Matcher intMatcher = INT_PATTERN.matcher(AssignmentToCheck);
                if (intMatcher.matches())
                    return true;
                return false;
            }
            case DOUBLE: {
                Matcher intMatcher = DOUBLE_PATTERN.matcher(AssignmentToCheck);
                if (intMatcher.matches())
                    return true;
                return false;

            }
            case STRING: {
                Matcher intMatcher = STRING_PATTERN.matcher(AssignmentToCheck);
                if (intMatcher.matches())
                    return true;
                return false;
            }
            case CHAR: {
                Matcher charMatcher = CHAR_PATTERN.matcher(AssignmentToCheck);
                if (charMatcher.matches())
                    return true;
                return false;
            }
            case BOOLEAN: {
                Matcher booleanMatcher = BOOLEAN_PATTERN.matcher(AssignmentToCheck);
                if (booleanMatcher.matches())
                    return true;
                Matcher doubleMatcher = DOUBLE_PATTERN.matcher(AssignmentToCheck);
                if (doubleMatcher.matches())
                    return true;
                return false;
            }
            default: {
                return false;
            }
        }


    }

    /**
     * Receive variable name, type, and assignment to the variable. Checks if the assignment is an existing variable
     * that we can use to assign. For example a=b, checks if b is a valid assignment for a. (does not check
     * if a is final!!!!)
     *
     * @param AssignmentToCheck the name of a possible variable (in our case above b)
     * @param type              the type of the current variable (in our case above a)
     * @param variablesToCheck  a hashmap of existing variables
     * @return true/false if it is an assignment or not
     * @throws CompileErrorException if a legal assignment was attempted
     */
    private static boolean checkLegalAssignmentHelper
    (String AssignmentToCheck, String type, HashMap<String, Variable> variablesToCheck)
            throws CompileErrorException {
        if (variablesToCheck.containsKey(AssignmentToCheck)) {
            if (variablesToCheck.get(AssignmentToCheck).isInitialization()) {
                if (type.equals(variablesToCheck.get(AssignmentToCheck).getType()))
                    return true;
                switch (type) {
                    case DOUBLE: {
                        if (variablesToCheck.get(AssignmentToCheck).getType().equals(INT))
                            return true;
                    }
                    case BOOLEAN: {
                        if (variablesToCheck.get(AssignmentToCheck).getType().equals(DOUBLE))
                            return true;
                        if (variablesToCheck.get(AssignmentToCheck).getType().equals(INT))
                            return true;
                    }
                    default: {
                        throw new CompileErrorException();  //if type assignment does not match
                    }
                }
            } else
                throw new CompileErrorException();  // if variable is not initialized
        }
        return false;
    }

    /**
     * Splits the type from variables
     *
     * @param currentLine the current line
     * @return a tuple of variables and type
     * @throws CompileErrorException if type doesn't match
     */
    private static String[] typeChecker(String currentLine) throws CompileErrorException {
        Matcher typeMatcher = TYPE_PATTERN.matcher(currentLine);
        if (!typeMatcher.matches())
            throw new CompileErrorException();
        String typeToInsert = currentLine.substring(typeMatcher.start(1), typeMatcher.end(1));
        typeToInsert = typeToInsert.trim();  // variable type
        String variables = currentLine.substring(typeMatcher.end(1));
        return new String[]{typeToInsert, variables};
    }

    /**
     * checks for an illegal comma syntax and splits possible variable in line
     *
     * @param variables a string of variables
     * @return the split list
     * @throws CompileErrorException
     */
    private static String[] illegalCommaChecker(String variables) throws CompileErrorException {
        Matcher illegalCommaCheck = COMMA_PATTERN.matcher(variables);  // edge case ",,"
        if (illegalCommaCheck.matches())
            throw new CompileErrorException();
        Matcher equleCommaMatcher = EQULE_COMMA_PATTERN.matcher(variables);
        if (equleCommaMatcher.matches()) //if there is a "," or "=" which we split with (example ab=)
            throw new CompileErrorException();
        return variables.split(",");
    }

    /**
     * checks if there is a final statement, if there is change the flag isFinal to true.
     *
     * @param currentLine the current line
     * @return true if there is a final statement else return false
     * @throws CompileErrorException
     */
    private static boolean finalHandle(String currentLine) throws CompileErrorException {
        boolean isFinal = false;
        Matcher finalMatcher = FINAL_PATTERN.matcher(currentLine);  //checks final
        if (finalMatcher.matches()) {
            isFinal = true;
            Matcher orderCheck = FINAL_STATEMENT_PATTERN.matcher(currentLine); //checks final + "type"
            if (!orderCheck.matches())
                throw new CompileErrorException();
        }
        return isFinal;
    }

    /**
     *
     *
     * @param currentLine the current line should be something like "a=b" (without type of a)
     * @return
     * @throws CompileErrorException
     */
    private static boolean AssignVariableToVariable(String currentLine , HashMap<String,GlobalVariable> variables )
            throws CompileErrorException {
        String[] lines = currentLine.split("=");
        String variable = lines[0];
        String AssignmentToCheck = lines[1];
        if(variables.containsKey(variable)) {
            GlobalVariable variableInHash = variables.get(variable);
            String type =variableInHash.getType();
            if(variableInHash.isFinal())
                throw new CompileErrorException();
            return checkLegalAssignment(AssignmentToCheck, type, variables);
        }
        throw new CompileErrorException();

    }



    /**
     * @param lineToRead
     * @return
     * @throws CompileErrorException
     */
    public static void isValidParameterVariable
    (String lineToRead, HashMap<String, Method> methodHashMap) throws CompileErrorException {
        lineToRead = lineToRead.trim();
//        if (lineToRead.endsWith(";"))
//            lineToRead = lineToRead.substring(0, lineToRead.length() - 1);
        //var to analyze are inside ()
        String parameters = methodChecker(lineToRead);
        String[] parametersList = parameters.split(",");
        for(String specificParameter : parametersList) {
            Matcher validVariableMatcher = VARIABLE_NAME_PATTERN.matcher(specificParameter);
            if (!validVariableMatcher.matches())
                throw new CompileErrorException();
            Matcher reservedKeyMatches = RESERVED_KEYWORDS_PATTERN.matcher(specificParameter); // check that the variable isn't a reserved word
            if (reservedKeyMatches.matches()) {
                throw new CompileErrorException();  // illegal variable name
            }
            boolean isFinal = finalHandle(specificParameter);
            if(isFinal)
                specificParameter = specificParameter.replaceFirst("final", "");
            String typeTuple[] = typeChecker(specificParameter);
            String typeToInsert = typeTuple[0], variable = typeTuple[1];


            methodHashMap.put(typeToInsert, new Method(typeToInsert, isFinal, variable));


        }
//        if (isFinal)
//            lineToRead = lineToRead.replaceFirst("final", "");
    }
//        String[] specificVariable = variableToAnalyze.split(",");
//        //todo split the param by comma
//        boolean isFinal = finalHandle(lineToRead);
//        if (isFinal)
//            lineToRead = lineToRead.replaceFirst("final", "");
////        Matcher validVariableWithNoTypeMatcher = VAR_TO_VAR_PATTERN.matcher(lineToRead);
////        //pattern to match a specific case a=b without type
////        if (validVariableWithNoTypeMatcher.matches()){
////            if(AssignVariableToVariable(lineToRead,globalHashMap))
////                return;
////            //todo check
////        }
//        String typeTuple[] = typeChecker(lineToRead);
//        String typeToInsert = typeTuple[0], variables = typeTuple[1];
//
//        String[] allVariables = illegalCommaChecker(variables);
//        for (String variableToAnalyze : allVariables) {
//            String[] specificVariable = variableToAnalyze.split("=");
//            int allowedLength = 2;
//            if (specificVariable.length > allowedLength || (isFinal && specificVariable.length == 1))
//                throw new CompileErrorException();
//            specificVariable[0] = specificVariable[0].trim();
//            Matcher validVariableMatcher = VARIABLE_NAME_PATTERN.matcher(specificVariable[0]);
//            if (validVariableMatcher.matches()) {
//                Matcher reservedKeyMatches = RESERVED_KEYWORDS_PATTERN.matcher(specificVariable[0]); // check that the variable isn't a reserved word
//                if (reservedKeyMatches.matches()) {
//                    throw new CompileErrorException();  // illegal variable name
//                }
//                boolean initialization = false;
//                if (specificVariable.length == 2) {
//                    initialization = true;
//                    if (!checkLegalAssignment(specificVariable[1], typeToInsert, globalHashMap)) //check the the type is legal
//                        throw new CompileErrorException();
//                } else if (isFinal)
//                    throw new CompileErrorException();
//                if (globalHashMap.containsKey(specificVariable[0])) {
//                    GlobalVariable existVar = globalHashMap.get(specificVariable[0]);
//                    if (existVar.isInitialization())
//                        throw new CompileErrorException();
//                }
//                //if the key exs but the existing global variable isn't initialize it ok
//                globalHashMap.put(specificVariable[0], new GlobalVariable(typeToInsert, initialization, isFinal, specificVariable[0]));
//            } else
//                throw new CompileErrorException();
//        }
////        return globals;
//    }
//
    public static String methodChecker(String lineToRead) throws CompileErrorException {
        Matcher methodMatches = METHOD_PATTERN.matcher(lineToRead); //check the case "void func(){" with spaces
        if (!methodMatches.matches()) {
            throw new CompileErrorException();
        }
        return methodMatches.group(4);
    }

    public static String isIfStatement(String lineToRead) throws CompileErrorException {
        Matcher ifMatcher = IF_PATTERN.matcher(lineToRead); //check the case "if(){" with spaces
        if (!ifMatcher.matches()) {
            throw new CompileErrorException();
        }
        return ifMatcher.group(1); //returns the statement inside if
    }

    public static String isWhileStatement(String lineToRead) throws CompileErrorException {
        Matcher whileMatcher = WHILE_PATTERN.matcher(lineToRead); //check the case "while(){" with spaces
        if (!whileMatcher.matches()) {
            throw new CompileErrorException();
        }
        return whileMatcher.group(1); //returns the statement inside while
    }

    public static String checkConditionInsideWhileIf(String lineToRead) throws CompileErrorException {
        String conditionIf = isIfStatement(lineToRead);
        String conditionWhile = isWhileStatement(lineToRead);
        Matcher booleanMatcher = BOOLEAN_PATTERN.matcher(lineToRead); //check the case "if(){" with spaces
        if (!booleanMatcher.matches()) {

    }


}
