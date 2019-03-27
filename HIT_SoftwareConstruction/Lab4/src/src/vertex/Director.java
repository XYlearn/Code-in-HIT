package vertex;

public class Director extends Person {
	
	/**
	 * AF, RI, safety from rep exposure: see Person
	 */

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name of actor
	 */
	protected Director(String name) {
		super(name);
	}
	
	@Override
	public Director clone() {
		return wrap(label, new String[] {sex, String.valueOf(age)});
	}
	
	/**
	 * Generate a Director Instance from given arguments
	 * 
	 * @param name
	 *            Acotr's name
	 * @param isMale
	 *            if the Director is Male, pass true; else pass false
	 * @param age
	 *            age of the Director
	 * @return return generated Person Instance if age >= 0. else return null.
	 */
	public static Director wrap(String name, boolean isMale, int age) {
		return (Director)Person.wrap(Director.class, name, isMale, age);
	}
	
	/**
	 * Generate a Director Instance from given arguments
	 * 
	 * @param label
	 *            name of Director
	 * @param args
	 *            argument to fill in see {@link Person#fillVertexInfo(String[])}
	 * @return return generated Person Instance if args are valid, else return null.
	 */
	public static Director wrap(String label, String[] args) {
		return (Director)Person.wrap(Director.class, label, args);
	}
}
