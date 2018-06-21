package oop.ex6;

import java.util.HashMap;
import java.util.LinkedList;

public class Method {

    private HashMap<String, LocalVariable> variablesInScope;

    private String methodName;

    private LinkedList<String> linesToRead;

    private HashMap<String, Method> methodNames;

    /**
     * Constructor for a method
     * @param variablesInScope
     * @param methodName
     */
    public Method(HashMap<String, LocalVariable> variablesInScope, String methodName, LinkedList<String>
            linesToRead, HashMap<String, Method> legalMethods){
        this.variablesInScope = variablesInScope;
        this.methodName = methodName;
        this.linesToRead = linesToRead;
        this.methodNames = legalMethods;
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
}
