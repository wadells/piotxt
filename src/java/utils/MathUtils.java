package utils;

/** A collection of mathematically-related utilities. */
public class MathUtils {

	/**
	 * Maps n into the appropriate equivalency class mod m, defined as 0,
	 * 1, ..., m - 1.
	 * 
	 * i.e. mod(-3, 5) == 2, rather than java's -3 % 5 == -3.
	 */
	public static int mod(int n, int m) {
		return (n % m + m) % m;
	}

}
