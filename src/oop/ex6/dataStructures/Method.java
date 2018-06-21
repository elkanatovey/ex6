package oop.ex6.dataStructures;

import oop.ex6.main.regexManager;
import oop.ex6.CompileErrorException;

import java.util.HashMap;
import java.util.LinkedList;

public class Method {

    private HashMap<String, LocalVariable> variablesInScope;

    private String methodName;

    private LinkedList<String> linesToRead;

    private HashMap<String, GlobalVariable> globals;

    private Method methodParent;

    private LinkedList<Method> methodLinkedList;

    private static final String RECURSIVE_CALL_NAME = "if/while";

    private String[] methodParameters;


    /**
     * Constructor for a method
     * @param variablesInScope
     * @param methodName
     */
    public Method(HashMap<String, LocalVariable> variablesInScope, String methodName,
                  LinkedList<String> linesToRead, LinkedList<Method> methodLinkedList,
                  LinkedList<String> methodParameters){
        this.variablesInScope = variablesInScope;
        this.methodName = methodName;
        this.linesToRead = linesToRead;
        this.globals = new HashMap<>();
        this.methodLinkedList = methodLinkedList;
        this.methodParent = null;  //default
        this.methodParameters = (String[]) methodParameters.toArray();
    }

    public String[] getMethodParameters() {
        return methodParameters;
    }

    /*
        constructor used for inner blocks
         */
    private Method(HashMap<String, LocalVariable> variablesInScope, String methodName,
                   LinkedList<String> linesToRead, Method methodParent){
        this(variablesInScope,methodName,linesToRead, methodParent.methodLinkedList,null);
        this.methodParent = methodParent;
    }


    /**
     * Check if a method has been declared
     * @param methodName
     * @param legalMethods
     * @return
     */
    public static boolean isLegalMethod(String methodName, LinkedList<Method> legalMethods){
        if(legalMethods.contains(methodName))
            return true;
        return false;
    }

    public void addLine(String lineToAdd){  // adds a new line to the method
        this.linesToRead.add(lineToAdd.trim());
    }

    public String getMethodName() {
        return methodName;
    }

    public HashMap<String, LocalVariable> getVariablesInScope() {
        return variablesInScope;
    }

    /**
     * Add a new local variable to the current scope
     * @param variableToAdd
     * @throws CompileErrorException
     */
    public void addLocalVariable(LocalVariable variableToAdd) throws CompileErrorException {
        if(this.variablesInScope.containsKey(variableToAdd.getName())) {
            throw new CompileErrorException();
        }
        this.variablesInScope.put(variableToAdd.getName(),variableToAdd);
    }

    /**
     * Checks if a variable exists within any of the relevant scopes
     * @param variableName
     * @return
     */
    public LocalVariable suchaVariableExists(String variableName){
        if (variablesInScope.containsKey(variableName))
            return variablesInScope.get(variableName);
        if(methodParent!=null){
            if (methodParent.getVariablesInScope().containsKey(variableName))
                return methodParent.getVariablesInScope().get(variableName);
            return methodParent.suchaVariableExists(variableName);
        }
        if (globals.containsKey(variableName))
            return new LocalVariable(globals.get(variableName).getType(),globals.get(variableName).isInitialization(),
                    globals.get(variableName).isFinal(),variableName);
        return null;
    }

    /**
     * returns a given variable iff it exists, else returns null
     * @param variableName
     * @return
     */
    public LocalVariable getLocalVariable(String variableName){
        if (variablesInScope.containsKey(variableName))
            return variablesInScope.get(variableName);
        return null;
    }

    /**
     * After a scope is initialized this method checks if t is legal
     * @param globals
     * @throws CompileErrorException
     */
    public void checkLegal(HashMap<String,GlobalVariable> globals) throws CompileErrorException{
        this.globals = globals;
        String lineToCheck = linesToRead.pollFirst();
        String previousLine = lineToCheck;
        while (lineToCheck!=null) {
            previousLine = lineToCheck;
            if (previousLine.contentEquals("}"))  // if we closed the block
                break;
            lineToCheck = linesToRead.pollFirst();
            if (regexManager.innerLineCheck(lineToCheck,this)) // read line and see case, if variable add-else call on new scope
                continue;
            HashMap<String, LocalVariable> valuesInOfScope = new HashMap<>();
            Method method = new Method(valuesInOfScope,RECURSIVE_CALL_NAME,this.linesToRead,this);
            method.checkLegal(globals);
        }
        if (this.methodParent!=null)
            if (linesToRead.peekFirst()==null)
                throw new CompileErrorException();
    }

    public LinkedList<String> getLinesToRead() {
        return linesToRead;
    }

    @Override
    public String toString() {
        return this.methodName;
    }

    @Override
    public boolean equals(Object o) {
        return methodName.equals(o.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
