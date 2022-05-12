
public class Application implements Expression {
    Expression left;
    Expression right;

    public Application(Expression left, Expression right){
        this.left = left;
        this.right = right;
    }

    public String toString(){
        return ("(" + left.toString() + " " + right.toString() + ")");
    }
}
