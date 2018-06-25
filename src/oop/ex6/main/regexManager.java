package oop.ex6.main;

import oop.ex6.*;
import oop.ex6.dataStructures.GlobalVariable;
import oop.ex6.dataStructures.LocalVariable;
import oop.ex6.dataStructures.Method;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.*;

import static java.util.regex.Pattern.compile;

public class regexManager {

    private static String CONDITIONS_INT_DOUBLE = "([+'-]?\\d*\\.?\\d+)|(\\s*\\d+\\s*)|(\\s*-\\d+\\s*)";
    private static String VARIABLE_NAME_STRING = "(\\s*\\D\\w*?_*\\s*)|(_\\w+)";
    private static final String INT = "int", DOUBLE = "double",
            STRING = "String", BOOLEAN = "boolean", CHAR = "char";
    private static final String BOOLEAN_PATTERN1 = ("(^|\\s+)(true|false)($|\\s+)");
    private static final Pattern INT_PATTERN = compile("(\\s*\\d+\\s*)|(\\s*-\\d+\\s*)");
    private static final Pattern DOUBLE_PATTERN = compile("[+'-]?\\d*\\.?\\d+");
    private static final Pattern STRING_PATTERN = compile("\\s*\".*(.*)+\\s*.*\"\\s*"); //how to add ""
    private static final Pattern BOOLEAN_PATTERN = compile("(^|\\s+)(true|false)($|\\s+)");  //todo debug
    private static final Pattern CALL_METHOD =
            compile("\\s*\\s*([a-zA-Z]\\w*)\\s*\\s*\\(\\s*((\\s*\\s*((([^,=\\s])+)|(\".*\"))\\s*\\s*)?|((\\s*\\s*((([^,=\\s])+)|(\".*\"))\\s*\\s*)(\\s*,\\s*\\s*((([^,=\\s])+)|(\".*\"))\\s*\\s*)*)\\s*)\\s*\\)\\s*;\\s*");
    private static final String FINAL_STATEMENT = ("\\s*(final\\s+.*)");
    private static String conditionStatement = "(true|false|-?[\\d]+|-?([\\d]+.[\\d]+)|(\\s*[A-Za-z]\\w*?_*\\s*)|(_\\w+))";
    private static final Pattern SPACE_PATTERN = compile("^\\s*$");
    private static final Pattern BACKSLASH_PATTERN = compile("^\\/\\/.*");
    private static final Pattern VARIABLE_NAME_PATTERN = compile(VARIABLE_NAME_STRING);
    private static final Pattern CONDITIONS_INT_DOUBLE_PATTERN = compile(CONDITIONS_INT_DOUBLE);
    private static final Pattern METHOD_PATTERN = compile("\\s*void\\s*(([A-Za-z]\\w*\\s*)|([_]\\w+\\s*))" +
            "\\s*[(](.*)[)]\\s*[{]");
    private static final Pattern RETURN_STATEMENT_PATTERN = compile("\\s*(return\\s*;\\s*)$");
    private static final Pattern IF_WHILE_PATTERN = compile("\\s*(if|while)\\s*[(](.*)[)]\\s*[{]");
    private static final Pattern FINAL_STATEMENT_PATTERN
            = compile("\\s*(final\\s*)\\s+((int|double|boolean|char|String)\\s+.*)$");
    //check if before final there spaces
    private static final Pattern TYPE_PATTERN = compile("\\s*((int|double|boolean|char|String)\\s+).*");
    private static final Pattern FINAL_PATTERN = compile(FINAL_STATEMENT);
    private static final Pattern COMMA_PATTERN = compile(".*,\\s*(,).*");
    private static final String RESERVED_KEYWORDS =
            "((\\w*\\s+)|\\s*)((int|double|boolean|char|String|void|final|if|while|true|false|return)\\s+.*)";
    private static final Pattern RESERVED_KEYWORDS_PATTERN = compile(RESERVED_KEYWORDS);
    private static final Pattern EQULE_COMMA_PATTERN = compile("\\w*((=)|,)\\s*");
    private static final Pattern CHAR_PATTERN = compile("'.'"); //no spaces inside
    private static final Pattern VAR_TO_VAR_PATTERN = compile("(\\s*\\D\\w*\\s*=\\D\\w*)|" +
            "(\\s*\\D\\w*\\s*=\\d\\s*)"); //a=b or a=5 without type
    private static int VARIABLE_EXISTS_IN_SCOPE = -1;
    private static int VARIABLE_EXISTS_NOT_IN_SCOPE = 0;
    private static int VARIABLE_DOESNT_EXIST = 1;


    /**
     * This function checks if a line is ignorable, and should be passed over
     *
     * @param currentLine the line to read
     * @return true/ false based upon if the line is ignorable
     */
    public static boolean isLineIgnorable(String currentLine) {
        if (currentLine == null)  //edge case null string
            return true;
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
        if (validVariableWithNoTypeMatcher.matches()) {
            if (AssignVariableToVariable(lineToRead, globalHashMap))
                return;
            //todo check
        }
        String typeTuple[] = typeChecker(lineToRead);
        String typeToInsert = typeTuple[0], variables = typeTuple[1];
        String[] allVariables = illegalCommaEqualsChecker(variables);
        for (String variableToAnalyze : allVariables) {
            addSingleVariable(variableToAnalyze, isFinal, globalHashMap, typeToInsert);
        }
//        return globals;
    }

    /**
     * Add a single new variable to the hashmap if it is legal, a legal variable is formatted in the
     * following manner: int a=1, int a; final int a; a=b is also legal if both exist, a is not final, b is
     * initialized, and they have the same type, however such a case will not be added to the hashmap,
     * although a will be updated to initialized.
     *
     * @param variableToAnalyze
     * @param isFinal
     * @param globalHashMap
     * @param typeToInsert
     * @throws CompileErrorException
     */
    private static void addSingleVariable(String variableToAnalyze, boolean isFinal, HashMap<String,
            GlobalVariable> globalHashMap, String typeToInsert)
            throws
            CompileErrorException {
        String[] specificVariable = variableToAnalyze.split("=");
        int allowedLength = 2;
        if (specificVariable.length > allowedLength || (isFinal && specificVariable.length == 1))
            throw new CompileErrorException();
        String variableName = specificVariable[0].trim();
        reservedKeyWordCheck(variableName);
        boolean initialization = false;
        if (specificVariable.length == 2) {
            initialization = true;
            if (!checkLegalAssignment(specificVariable[1], typeToInsert, globalHashMap))
                //check the the type is legal
                throw new CompileErrorException();
        } else if (isFinal)
            throw new CompileErrorException();
        if (globalHashMap.containsKey(variableName)) {
            GlobalVariable existVar = globalHashMap.get(variableName);
            if (existVar.isInitialization())
                throw new CompileErrorException();
        }
        //if the key exs but the existing global variable isn't initialized it ok
        globalHashMap.put(variableName, new GlobalVariable(typeToInsert, initialization,
                isFinal, variableName));
        //todo specificVariable[0] twice ? type is the name, therefore twice
    }


    /*
    checks that a given variable name is legal, if is illegal throws an exception
     */
    private static void reservedKeyWordCheck(String stringToCheck) throws CompileErrorException {
        Matcher validVariableMatcher = VARIABLE_NAME_PATTERN.matcher(stringToCheck);
        if (validVariableMatcher.matches()) {
            Matcher reservedKeyMatches = RESERVED_KEYWORDS_PATTERN.matcher(stringToCheck);
            // check that the variable isn't a reserved word
            if (reservedKeyMatches.matches()) {
                throw new CompileErrorException();  // illegal variable name
            }
        } else
            throw new CompileErrorException();

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
        if (checkLegalAssignmentHelper(AssignmentToCheck, type, globals)) {
            return true;
        }
        return matchTypeToValue(type, AssignmentToCheck);
    }

    /*matches a given type to an appropriate value*/
    private static boolean matchTypeToValue(String type, String AssignmentToCheck) {
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
     * Receive variable name, type, and assignment to the variable. Checks if the assignment is an existing
     * variable
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
    (String AssignmentToCheck, String type, HashMap<String, GlobalVariable> variablesToCheck)
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
    private static String[] illegalCommaEqualsChecker(String variables) throws CompileErrorException {
        Matcher illegalCommaCheck = COMMA_PATTERN.matcher(variables);  // edge case ",,"
        if (illegalCommaCheck.matches())
            throw new CompileErrorException();
        Matcher equleCommaMatcher = EQULE_COMMA_PATTERN.matcher(variables);
        if (equleCommaMatcher.matches()) //if there is a "," or "=" which we split with (example ab=)
            throw new CompileErrorException();
        String[] variableArray = variables.split(",");
        for (String variable : variableArray) {
            if (variable.equals("") && variableArray.length > 1)
                throw new CompileErrorException();
        }
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
     * @param currentLine the current line should be something like "a=b" (without type of a)
     * @return
     * @throws CompileErrorException
     */
    private static boolean AssignVariableToVariable(String currentLine, HashMap<String, GlobalVariable> variables)
            throws CompileErrorException {
        illegalCommaEqualsChecker(currentLine);
        String[] lines = currentLine.split("=");
        String variable = lines[0];
        String AssignmentToCheck = lines[1];
        if (variables.containsKey(variable)) {
            GlobalVariable variableInHash = variables.get(variable);
            String type = variableInHash.getType();
            if (variableInHash.isFinal())
                throw new CompileErrorException();
            if (checkLegalAssignment(AssignmentToCheck, type, variables)) {
                variables.get(variable).setInitialization(true);
                return true;
            }
            return false;
        }
        throw new CompileErrorException();

    }


    /**
     * Evaluate a methods parameters and add them and the method to method list
     *
     * @param lineToRead
     * @return
     * @throws CompileErrorException
     */
    public static void isValidParameterVariable
    (String lineToRead, LinkedList<Method> methodLinkedList, HashMap<String, LocalVariable> localHashMap)
            throws CompileErrorException {
        lineToRead = lineToRead.trim();
        String[] parametersMethodArray = methodPatternChecker(lineToRead);  // todo fix
        String parameters = parametersMethodArray[0], methodName = parametersMethodArray[1];
        String[] parametersList = illegalCommaEqualsChecker(parameters);
        LinkedList<String> parametersTypeList = new LinkedList<>();
        LinkedList<String> methodLines = new LinkedList<String>();
        Method throwawayMethod = new Method(localHashMap, methodName, methodLines, methodLinkedList,  //
                // used to access method resources but not finalized
                parametersTypeList);
        for (String specificParameter : parametersList) {
            if (!specificParameter.trim().equals("")) {  //todo check
                if (isValidParameterVariableHelper
                        (specificParameter, parametersTypeList, localHashMap, throwawayMethod) ==
                        VARIABLE_EXISTS_IN_SCOPE)
                    throw new CompileErrorException(); //adds relevant objects to hashmaps
            }
        }
        Method method = new Method(localHashMap, methodName, methodLines, methodLinkedList,
                parametersTypeList);
        if (methodLinkedList.contains(method))
            throw new CompileErrorException();
        methodLinkedList.add(method);
    }

    /*Add a local variable to a hashmap if it doesn't exist in current scope, returns -1,0 or 1 based upon
    if the variable exists in current/outer scope*/
    private static int isValidParameterVariableHelper(String specificParameter, LinkedList<String>
            parametersTypeList, HashMap<String, LocalVariable> localHashMap, Method method)
            throws
            CompileErrorException {
        boolean isFinal = finalHandle(specificParameter);
        if (isFinal)
            specificParameter = specificParameter.replaceFirst("final", "");
        String typeTuple[] = typeChecker(specificParameter);
        String typeToInsert = typeTuple[0].trim(), variableName = typeTuple[1].trim();
        parametersTypeList.add(typeToInsert);
        reservedKeyWordCheck(variableName);
        LocalVariable localVar = new LocalVariable(typeToInsert, true, isFinal, variableName);
        if (localHashMap.containsKey(variableName))//check a local variable is not in the hash
            return VARIABLE_EXISTS_IN_SCOPE;
        if (method.suchVariableExists(localVar.getName()) != null) {
            localHashMap.put(variableName, localVar); //add the local variable (parameters) of a method
            return VARIABLE_EXISTS_NOT_IN_SCOPE;  //false if in further out scope
        }
        localHashMap.put(variableName, localVar);
        return VARIABLE_DOESNT_EXIST;
    }

    /**
     * Check that the current line is a legal method declaration
     *
     * @param lineToRead
     * @return
     * @throws CompileErrorException
     */
    public static String[] methodPatternChecker(String lineToRead) throws CompileErrorException {
        lineToRead = lineToRead.trim();
        Matcher methodMatches = METHOD_PATTERN.matcher(lineToRead); //check the case "void func(){" with spaces
        if (!methodMatches.matches()) {
            throw new CompileErrorException();
        }
        String parameters = methodMatches.group(4);
        String methodName = methodMatches.group(1);
        return new String[]{parameters, methodName}; // returns the name and parameters as an index
    }


    /**
     * checks if the line matches an if/while pattern
     *
     * @param lineToRead the line to check
     * @return returns the parameters inside the statement
     * @throws CompileErrorException
     */
    public static String isIfWhileStatement(String lineToRead) throws CompileErrorException {
        lineToRead = lineToRead.trim();
        Matcher ifWhileMatcher = IF_WHILE_PATTERN.matcher(lineToRead); //check the case "if(){" with spaces
        if (!ifWhileMatcher.matches()) {
            throw new CompileErrorException();
        }
        return ifWhileMatcher.group(2); //returns the statement inside if
    }


    /*
    This function checks that the inner condition of an if/while statement is valid
     */
    public static void checkConditionInsideWhileIf(String lineToRead, Method currentMethod) throws CompileErrorException {
        String conditionIf = isIfWhileStatement(lineToRead).trim();
        String[] conditionsArray = conditionIf.split("(\\|\\s*\\|)|(\\&\\s*\\&)");
        for (String param : conditionsArray) {
            param = param.trim();
            Matcher booleanMatcher = BOOLEAN_PATTERN.matcher(param); //condition : (true\false)
            if (!booleanMatcher.matches()) {
                Matcher intDoubleMatcher = CONDITIONS_INT_DOUBLE_PATTERN.matcher(param); //condition : (5)
                if (!intDoubleMatcher.matches()) {
                    Matcher booleanVarMatcher = VARIABLE_NAME_PATTERN.matcher(param); //condition : (a)
                    if (booleanVarMatcher.matches()) {
                        if (!conditionCaseVariable(param, currentMethod))
                            throw new CompileErrorException();
                    } else
                        throw new CompileErrorException();
                }
            }
        }
        return;
    }


    //*check the case if(a) when a is a variable*/
    private static boolean conditionCaseVariable(String lineToRead, Method currentMethod) throws CompileErrorException {
        reservedKeyWordCheck(lineToRead);
        LocalVariable variable = currentMethod.suchVariableExists(lineToRead);
        if (variable == null || !variable.isInitialization())
            throw new CompileErrorException();
        String typeToCheck = variable.getType();
        if (typeToCheck.equals("boolean") | typeToCheck.equals("int") || typeToCheck.equals("double")) {
            return true;

        }

        return false;
    }

    /*
    check if the inner lines of a block are legal, if yes adds them to the current block
     */
    public static boolean innerLineCheck(String lineToRead, Method method) throws CompileErrorException {
        char endOfLine = lineToRead.charAt(lineToRead.length() - 1);
        switch (endOfLine) {
            case ';': {
                Matcher returnMatcher = RETURN_STATEMENT_PATTERN.matcher(lineToRead);
                if (returnMatcher.matches())
                    return true;
                if (innerSemiColonCheck(lineToRead, method))
                    return true;
                isValidLocalVariable(lineToRead, method);
                return true;
            }
            //a=b matcher
            //int a; matcher
            //int a=b; matcher

            case '{':
                checkConditionInsideWhileIf(lineToRead, method);
                return false;
            default: {
                throw new CompileErrorException();
            }
        }
    }

    private static void isValidLocalVariable
            (String lineToRead, Method method) throws CompileErrorException {
        lineToRead = lineToRead.trim();
        if (lineToRead.endsWith(";"))
            lineToRead = lineToRead.substring(0, lineToRead.length() - 1);
        boolean isFinal = finalHandle(lineToRead);
        if (isFinal)
            lineToRead = lineToRead.replaceFirst("final", "");
        Matcher validVariableWithNoTypeMatcher = VAR_TO_VAR_PATTERN.matcher(lineToRead);
        //pattern to match a specific case a=b without type
        if (validVariableWithNoTypeMatcher.matches()) {
            if (AssignLocalVariableToVariable(lineToRead, method))
                return;
            //todo check
        }
        String typeTuple[] = typeChecker(lineToRead);
        String typeToInsert = typeTuple[0], variables = typeTuple[1];
        String[] allVariables = illegalCommaEqualsChecker(variables);
        for (String variableToAnalyze : allVariables) {
            addSingleLocalVariable(variableToAnalyze, isFinal, method, typeToInsert);
        }
    }

    private static void addSingleLocalVariable(String variableToAnalyze, boolean isFinal, Method method,
                                               String typeToInsert)
            throws
            CompileErrorException {
        if (method.getLocalVariable(variableToAnalyze) != null)
            throw new CompileErrorException();
        String[] specificVariable = variableToAnalyze.split("=");
        int allowedLength = 2;
        if (specificVariable.length > allowedLength || (isFinal && specificVariable.length == 1))
            throw new CompileErrorException();
        String variableName = specificVariable[0].trim();
        reservedKeyWordCheck(variableName);
        boolean initialization = false;
        if (specificVariable.length == 2) {
            initialization = true;
            if (!checkLocalLegalAssignment(specificVariable[1], typeToInsert, method))
                //check the the type is legal
                throw new CompileErrorException();
        } else if (isFinal)
            throw new CompileErrorException();
        if (method.suchVariableExists(variableName) != null) {
            LocalVariable existVar = method.suchVariableExists(variableName);
            if (existVar.isInitialization())//todo is final?
                throw new CompileErrorException();
        }
        //if the key exs but the existing global variable isn't initialized it ok
        method.addUpdateLocalVariable(new LocalVariable(typeToInsert, initialization, isFinal, variableName));
        //todo specificVariable[0] twice ? type is the name, therefore twice
    }


    private static boolean AssignLocalVariableToVariable(String currentLine, Method method)
            throws CompileErrorException {
        illegalCommaEqualsChecker(currentLine);
        String[] lines = currentLine.split("=");
        String variable = lines[0];
        String AssignmentToCheck = lines[1];
        if (method.suchVariableExists(variable) != null) {
            LocalVariable variableInHash = method.suchVariableExists(variable);
            String type = variableInHash.getType();
            if (variableInHash.isFinal())
                throw new CompileErrorException();
            if (checkLocalLegalAssignment(AssignmentToCheck, type, method)) {
                method.addUpdateLocalVariable(new LocalVariable(type, true, false, variable));  //todo make
                // actually
                // local
                return true;
            }
            return false;
        }
        throw new CompileErrorException();

    }


    private static boolean checkLocalLegalAssignment
            (String AssignmentToCheck, String type, Method method) throws CompileErrorException {
        AssignmentToCheck = AssignmentToCheck.trim();
        if (checkLocalLegalAssignmentHelper(AssignmentToCheck, type, method)) {
            return true;
        }
        return matchTypeToValue(type, AssignmentToCheck);
    }


    /**
     * Receive variable name, type, and assignment to the variable. Checks if the assignment is an existing
     * variable
     * that we can use to assign. For example a=b, checks if b is a valid assignment for a. (does not check
     * if a is final!!!!)
     *
     * @param AssignmentToCheck the name of a possible variable (in our case above b)
     * @param type              the type of the current variable (in our case above a)
     * @param method            the current scope
     * @return true/false if it is an assignment or not
     * @throws CompileErrorException if a legal assignment was attempted
     */
    private static boolean checkLocalLegalAssignmentHelper
    (String AssignmentToCheck, String type, Method method)
            throws CompileErrorException {
        if (method.suchVariableExists(AssignmentToCheck) != null) {
            if (method.suchVariableExists(AssignmentToCheck).isInitialization()) {
                if (type.equals(method.suchVariableExists(AssignmentToCheck).getType()))
                    return true;
                switch (type) {
                    case DOUBLE: {
                        if (method.suchVariableExists(AssignmentToCheck).getType().equals(INT))
                            return true;
                    }
                    case BOOLEAN: {
                        if (method.suchVariableExists(AssignmentToCheck).getType().equals(DOUBLE))
                            return true;
                        if (method.suchVariableExists(AssignmentToCheck).getType().equals(INT))
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


    /*Check if this is calling a function, return true false if it is, if illegal call throw exception*/
    private static boolean innerSemiColonCheck(String lineToRead, Method method) throws CompileErrorException {
        Matcher methodCallMatcher = CALL_METHOD.matcher(lineToRead);
        if (methodCallMatcher.matches()) {// match group 1
            String methodName = methodCallMatcher.group(1);
            Method methodCalled = method.isLegalMethod(methodName);
            if (methodCalled != null) {
                String parameters = methodCallMatcher.group(2);
                String[] parameterArray = parameters.split(",");
                if (parameterArray.length != methodCalled.getMethodParametersType().length) {
                    if (parameterArray.length == 1 && parameterArray[0].equals("") && methodCalled
                            .getMethodParametersType().length == VARIABLE_DOESNT_EXIST)
                        return true;  // edge case - no parameters in method
                    else
                        throw new CompileErrorException();
                }
                for (int i = 0; i < parameterArray.length; i++) {
                    Matcher isVariableName = VARIABLE_NAME_PATTERN.matcher(parameterArray[i].trim());
                    if (!compareParameterToType(isVariableName, parameterArray[i], methodCalled, method, i)) {
                        if (!matchTypeToValue(methodCalled.getMethodParametersType()[i], parameterArray[i]))
                            throw new CompileErrorException();
                    }
                }
                return true;
            } else {
                throw new CompileErrorException(); //illegal method
            }
        }
        return false;
    }
//todo fix
    /*checks if the parameter is an existing variable*/
    private static boolean compareParameterToType(Matcher isVariableName, String parameter, Method methodCalled,
                                                  Method
                                                          method, int i) throws
            CompileErrorException {
        if (isVariableName.matches()) {
            parameter = parameter.trim();
            LocalVariable parameterToCheck = method.suchVariableExists(parameter);  //illegal case
            if (parameterToCheck != null && methodCalled.getMethodParametersType()[i].equals
                    (parameterToCheck.getType()) && parameterToCheck.isInitialization())
                return true;
            else
                throw new CompileErrorException();
        }
        return false;  //different pattern
    }
}