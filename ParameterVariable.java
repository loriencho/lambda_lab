//Satwika Vemuri and Lorien Cho

import java.util.HashSet;
import java.util.Set;

public class ParameterVariable extends Variable{

    public Set<BoundVariable> boundVars;
    
    public ParameterVariable(String name, Set<BoundVariable> boundVars){
        super(name);
        this.boundVars = boundVars;
    }

    public ParameterVariable(String name){
        super(name);
        this.boundVars = new HashSet<BoundVariable>();
    }
    public void addBoundVariable(BoundVariable b){
        boundVars.add(b);
    }

    public Set<BoundVariable> getBoundVars(){
        return boundVars;
    }

    public Boolean equals(Expression exp){
        if(exp instanceof ParameterVariable){
            ParameterVariable pv = (ParameterVariable) exp;
            return (boundVars.equals(pv.getBoundVars()) 
                && this.toString().equals(pv.toString()));
        }
        
        return false;
    }
}