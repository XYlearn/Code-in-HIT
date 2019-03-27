package vertex;

public class Actor extends Person {

	/**
	 * AF, RI, safety from rep exposure: see Person
	 */

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name of actor
	 */
	protected Actor(String name) {
		super(name);
	}
	
	@Override
	public Actor clone() {
		return wrap(label, new String[] {sex, String.valueOf(age)});
	}
	
	/**
	 * Generate a Actor Instance from given arguments
	 * 
	 * @param name
	 *            Acotr's name
	 * @param isMale
	 *            if the Actor is Male, pass true; else pass false
	 * @param age
	 *            age of the Actor
	 * @return return generated Person Instance if age >= 0. else return null.
	 */
	public static Actor wrap(String name, boolean isMale, int age) {
		return Actor.class.cast(Person.wrap(Actor.class, name, isMale, age));
	}
	
	/**
	 * Generate a Actor Instance from given arguments
	 * 
	 * @param label
	 *            name of Actor
	 * @param args
	 *            argument to fill in see {@link Person#fillVertexInfo(String[])}
	 * @return return generated Person Instance if args are valid, else return null.
	 */
	public static Actor wrap(String label, String[] args) {
		return Actor.class.cast(Person.wrap(Actor.class, label, args));
	}

}
