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

    private HashMap<String, Method> methodHashMap;

    private static final String RECURSIVE_CALL_NAME = "if/while";


    /**
     * Constructor for a method
     * @param variablesInScope
     * @param methodName
     */
    public Method(HashMap<String, LocalVariable> variablesInScope, String methodName,
                  LinkedList<String> linesToRead, HashMap<String, Method> methodHashMap){
        this.variablesInScope = variablesInScope;
        this.methodName = methodName;
        this.linesToRead = linesToRead;
        this.globals = new HashMap<>();
        this.methodHashMap = methodHashMap;
        this.methodParent = null;  //default
    }

    /*
    constructor used for inner blocks
     */
    private Method(HashMap<String, LocalVariable> variablesInScope, String methodName,
                   LinkedList<String> linesToRead, Method methodParent){
        this(variablesInScope,methodName,linesToRead, methodParent.methodHashMap);
        this.methodParent = methodParent;
    }


    /**
     * Check if a method has been declared
     * @param methodName
     * @param legalMethods
     * @return
     */
    public static boolean isLegalMethod(String methodName, HashMap<String,Method> legalMethods){
        if(legalMethods.containsKey(methodName))
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

    public void addLocalVariable(LocalVariable variableToAdd) throws CompileErrorException {
        if(this.variablesInScope.containsKey(variableToAdd.getName())) {
            throw new CompileErrorException();
        }
        this.variablesInScope.put(variableToAdd.getName(),variableToAdd);
    }

    public LocalVariable parentContainsVariable(String variableName){
        if(methodParent!=null){
            if (methodParent.getVariablesInScope().containsValue(variableName))
                return methodParent.getVariablesInScope().get(variableName);
            return methodParent.parentContainsVariable(variableName);
        }
        return null;
    }

    public void checkLegal(HashMap<String,GlobalVariable> globals) throws CompileErrorException{
        this.globals = globals;
        String lineToCheck = linesToRead.pollFirst();
        String previousLine = lineToCheck;
        while (lineToCheck!=null) {
            previousLine = lineToCheck;
            if (previousLine.contentEquals("}"))  // if we closed the block
                break;
            lineToCheck = linesToRead.pollFirst();
            if (regexManager.) // read line and see case, if variable add-else call on new scope
                continue;
            HashMap<String, LocalVariable> valuesInOfScope = new HashMap<>();
            Method method = new Method(valuesInOfScope,RECURSIVE_CALL_NAME,this.linesToRead,this);
            method.checkLegal(globals);
        }
        if (this.methodParent!=null)
            if (linesToRead.peekFirst()==null)
                throw new CompileErrorException();
    }

    //if and while are inside method
    //if final the variable cant be changed inside the method
    //handle recursive calls
    //different name to method
    //no need to declare method inside another (calling a method only inside another method)
    //aaa


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
