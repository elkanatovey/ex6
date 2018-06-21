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

    private int scope;



    /**
     * Constructor for a method
     * @param variablesInScope
     * @param methodName
     */
    public Method(HashMap<String, LocalVariable> variablesInScope, String methodName,
                  LinkedList<String> linesToRead){
        this.variablesInScope = variablesInScope;
        this.methodName = methodName;
        this.linesToRead = linesToRead;
        this.globals = new HashMap<>();
        this.scope = 0;  //default
    }

    /*
    constructor used for inner blocks
     */
    private Method(HashMap<String, LocalVariable> variablesInScope, String methodName,
                   LinkedList<String> linesToRead, int scope){
        this(variablesInScope,methodName,linesToRead);
        this.scope = this.scope +scope;
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
            HashMap<String, Variable> valuesOutOfScope = new HashMap<>();
            for (String key: this.variablesInScope.keySet()) {
                valuesOutOfScope.put(key, variablesInScope.get(key).);
            }

            Method method = new Method();
            method.checkLegal(valuesOutOfScope);
        }
        if (this.scope!=0)
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
