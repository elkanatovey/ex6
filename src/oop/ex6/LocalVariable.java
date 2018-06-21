package oop.ex6;


public class LocalVariable extends  Variable {

    private int scope;

    public LocalVariable(String type, boolean initialized, boolean isFinal, int outerMostScope, String name){
        super(type, initialized,isFinal, name);
        scope = outerMostScope;
    }

    public int getScope() {
        return scope;
    }
}
