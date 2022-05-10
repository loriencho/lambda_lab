import java.util.ArrayList;

public class ParameterVariable extends Variable{
    public ArrayList<BoundVariable> boundVars;

        public ParameterVariable(String name, ArrayList<BoundVariable> boundVars){
        super(name);
        this.boundVars = boundVars;
    }
}