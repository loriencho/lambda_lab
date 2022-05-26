
public class Application implements Expression {
    private Expression left;
    private Expression right;

    public Application(Expression left, Expression right){
        this.left = left;
        this.right = right;
    }

    public Expression getLeft(){
        return left; 
    }

    public Expression getRight(){
        return right; 
    }

    public Expression setLeft(Expression newLeft){
        this.left = newLeft;
        return left;
    }

    public Expression setRight(Expression newRight){
        this.right = newRight;
        return right; 
    }
    public String toString(){
        return ("(" + left.toString() + " " + right.toString() + ")");
    }

    public Boolean equals(Application app){
        if((app.getLeft().equals(this.getLeft())) && (app.getRight().equals(this.getRight()))){
            return true;
        }
        return false;
    }
}
