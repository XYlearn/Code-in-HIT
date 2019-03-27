package twitter;

/**
 * utils for twitter
 * @author 
 *
 */
public class TwitterUtils {
	/**
	 * check if character a legal twitter name character
	 * @param ch character to check
	 * @return whether ch is legal name character
	 */
	public static boolean isLegalNameChar(char ch) {
		return Character.isAlphabetic(ch) || ch=='_' || ch=='-';
	}
}
