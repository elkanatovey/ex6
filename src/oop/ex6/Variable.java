package oop.ex6;

public abstract class Variable {




    private boolean initialization;
    private String type;
    private boolean isFinal;
    private String name;

    public Variable(String type, boolean initialized, boolean isFinal, String name){
        this.type = type;
        this.initialization = initialized;
        this.isFinal = isFinal;
        this.name=name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isInitialization() {
        return initialization;
    }

    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Throw an exception if an attempted variable assignment was illegal;
     * @param newVariable
     * @throws illegalAssignmentException
     */
    public void setType(Variable newVariable) throws illegalAssignmentException{ // maybe change to true/false?
        if (!type.equals(newVariable.getType()))  // to update
            throw new illegalAssignmentException();
        if (this.isFinal())
            throw new  illegalAssignmentException();
        if (!newVariable.isInitialization())
            throw new illegalAssignmentException();
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }

    public void setInitialization(boolean initialization) {
        this.initialization = initialization;
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return name.equals(o.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
