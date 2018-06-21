package oop.ex6.dataStructures;


public class LocalVariable extends Variable {

    private int scope;

    public LocalVariable(String type, boolean initialized, boolean isFinal, String name){
        super(type, initialized,isFinal, name);
    }

    public int getScope() {
        return scope;
    }
}
