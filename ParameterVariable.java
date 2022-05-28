import java.util.ArrayList;

public class ParameterVariable extends Variable{

    public ArrayList<BoundVariable> boundVars;
    
    public ParameterVariable(String name, ArrayList<BoundVariable> boundVars){
        super(name);
        this.boundVars = boundVars;
    }

    public ParameterVariable(String name){
        super(name);
        this.boundVars = new ArrayList<BoundVariable>();
    }
    public void addBoundVariable(BoundVariable b){
        boundVars.add(b);
    }

    public ArrayList<BoundVariable> getBoundVars(){
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