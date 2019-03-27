package vertex;

/**
 * Represent Movie in MovieGraph. It has Information about the Moview's name,
 * release date, country it from and IMDb grade.
 */
public class Movie extends Vertex {

	private int releaseDate;
	private String country;
	private float grade;

	/**
	 * AF: releaseDate:int, country:String, grade: float with scale at most 2
	 * 
	 * RI: releaseDate in range [1900, 2018]; grade in range [0, 10]
	 * 
	 * safety from rep exposure: all added fields are private. and not modifiable
	 */

	/**
	 * Constructor of Movie
	 * 
	 * @param label
	 *            name of the Movie
	 */
	protected Movie(String label) {
		super(label);
	}
	
	public void checkRep() {
		assert releaseDate >= 1900 && releaseDate <= 2018;
		assert grade >=0 && grade <= 10;
		assert !country.isEmpty();
	}

	/**
	 * get release date of Movie
	 * 
	 * @return release date of Movie
	 */
	public int getReleaseDate() {
		return releaseDate;
	}

	/**
	 * get country of Movie
	 * 
	 * @return country of Movie
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * get grade of movie in IMDb
	 * 
	 * @return grade of Movie in IMDb
	 */
	public float getGrade() {
		return grade;
	}

	/**
	 * Add detail information of Movie.The information is release date, country it
	 * comes from and the grade on IMDb
	 * 
	 * @param args
	 *            the String format of arguments to fill. args[0] is release Date,
	 *            format in Decimal number string in range of [1900, 2018]; args[1]
	 *            is country the Movie is froml; args[2] is Movie's grade on IMDb
	 * 
	 * @exception IllegalArgumentException
	 *                raised when the arguments are not legal, and will do nothing
	 *                if this occurs
	 */
	@Override
	public void fillVertexInfo(String[] args) throws IllegalArgumentException {
		if (args.length < 3)
			throw new IllegalArgumentException("Invalid Argument. Expected at least 3 arguments");
		// parse releaseDate
		String releaseDateStr = args[0];
		try {
			int releaseDate = Integer.parseInt(releaseDateStr);
			// check the range
			if (releaseDate < 1900 || releaseDate > 2018)
				throw new RuntimeException();
			else
				this.releaseDate = releaseDate;
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid Argument. Date is expected an integer in range[1900,2018]");
		}
		// get contry
		this.country = args[1];

		// parse Grade
		String gradeStr = args[2];
		try {
			float grade = Float.parseFloat(gradeStr);
			// check the range
			if (grade > 10 || grade < 0)
				throw new RuntimeException();
			// set Scale
			this.grade = Float.parseFloat(String.format("%.2f", grade));
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid Argument. Grade is expected an float number in range[0,10]");
		}
		checkRep();
	}

	@Override
	public Movie clone() {
		String[] args = new String[] { String.valueOf(releaseDate), country, String.valueOf(grade) };
		return wrap(label, args);
	}

	@Override
	public String toString() {
		return "<" + label + ">";
	}

	/**
	 * Create a Movie Instance
	 * 
	 * @param label
	 *            name of movie
	 * @param args
	 *            arguments to fill. args[0] is release Date, format in Decimal
	 *            number string in range of [1900, 2018]; args[1] is country the
	 *            Movie is froml; args[2] is Movie's grade on IMDb
	 * @return if the arguments are all valid, return an instance of Movie, else
	 *         return null
	 */
	public static Movie wrap(String label, String[] args) {
		Movie movie = new Movie(label);
		try {
			movie.fillVertexInfo(args);
		} catch (IllegalArgumentException e) {
			return null;
		}
		movie.checkRep();
		return movie;
	}
}
