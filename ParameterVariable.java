import java.util.ArrayList;

public class ParameterVariable extends Variable{

    // Function it's bound to
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
}