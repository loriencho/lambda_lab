
public abstract class Variable implements Expression {
	protected String name;
	
	public Variable(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}

	public void setName(String newName){
        this.name = newName;
    }

	public boolean equals(Variable variable){
		return (variable.toString()).equals(this.toString());
	}

}
