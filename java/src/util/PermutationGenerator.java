package util;

// http://www.merriampark.com/perm.htm

//--------------------------------------
// Systematically generate permutations. 
//--------------------------------------

import java.math.BigInteger;
import java.util.Iterator;

public class PermutationGenerator implements Iterable<int[]> {

	private static BigInteger getFactorial(int n) {
		BigInteger fact = BigInteger.ONE;
		for (int i = n; i > 1; i--) {
			fact = fact.multiply(new BigInteger(Integer.toString(i)));
		}
		return fact;
	}

	private final int[] a;
	private BigInteger numLeft;
	private final BigInteger total;

	// -----------------------------------------------------------
	// Constructor. WARNING: Don't make n too large.
	// Recall that the number of permutations is n!
	// which can be very large, even when n is as small as 20 --
	// 20! = 2,432,902,008,176,640,000 and
	// 21! is too big to fit into a Java long, which is
	// why we use BigInteger instead.
	// ----------------------------------------------------------

	private boolean iterated = false;

	// ------
	// Reset
	// ------

	public PermutationGenerator(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("Min 1");
		}
		this.a = new int[n];
		this.total = getFactorial(n);
		reset();
	}

	// ------------------------------------------------
	// Return number of permutations not yet generated
	// ------------------------------------------------

	public int[] getNext() {

		if (this.numLeft.equals(this.total)) {
			this.numLeft = this.numLeft.subtract(BigInteger.ONE);
			return this.a;
		}

		int temp;

		// Find largest index j with a[j] < a[j+1]

		int j = this.a.length - 2;
		while (this.a[j] > this.a[j + 1]) {
			j--;
		}

		// Find index k such that a[k] is smallest integer
		// greater than a[j] to the right of a[j]

		int k = this.a.length - 1;
		while (this.a[j] > this.a[k]) {
			k--;
		}

		// Interchange a[j] and a[k]

		temp = this.a[k];
		this.a[k] = this.a[j];
		this.a[j] = temp;

		// Put tail end of permutation after jth position in increasing order

		int r = this.a.length - 1;
		int s = j + 1;

		while (r > s) {
			temp = this.a[s];
			this.a[s] = this.a[r];
			this.a[r] = temp;
			r--;
			s++;
		}

		this.numLeft = this.numLeft.subtract(BigInteger.ONE);
		return this.a;

	}

	// ------------------------------------
	// Return total number of permutations
	// ------------------------------------

	public BigInteger getNumLeft() {
		return this.numLeft;
	}

	// -----------------------------
	// Are there more permutations?
	// -----------------------------

	public BigInteger getTotal() {
		return this.total;
	}

	// ------------------
	// Compute factorial
	// ------------------

	public boolean hasMore() {
		return this.numLeft.compareTo(BigInteger.ZERO) == 1;
	}

	// --------------------------------------------------------
	// Generate next permutation (algorithm from Rosen p. 284)
	// --------------------------------------------------------

	@Override
	public Iterator<int[]> iterator() {
		if (this.iterated) {
			return null;
		}
		this.iterated = true;
		return new Iterator<int[]>() {

			@Override
			public boolean hasNext() {
				return hasMore();
			}

			@Override
			public int[] next() {
				return getNext();
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				throw new UnsupportedOperationException(
						"The method Iterator<int[]>.remove is not yet implemented");
				//
			}

		};
	}

	public void reset() {
		for (int i = 0; i < this.a.length; i++) {
			this.a[i] = i;
		}
		this.numLeft = new BigInteger(this.total.toString());
	}

}