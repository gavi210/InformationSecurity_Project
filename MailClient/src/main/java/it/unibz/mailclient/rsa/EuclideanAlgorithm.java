package it.unibz.mailclient.rsa;

public class EuclideanAlgorithm {

	public static int getPrivateKeyValue(int a, int b) {
		a = a % b;
		for (int x = 1; x < b; x++)
			if ((a * x) % b == 1)
				return x;
		return 1;
	}

	public static int gcdEuclidean(int n1, int n2) {
		if (n2 == 0) {
			return n1;
		}
		return EuclideanAlgorithm.gcdEuclidean(n2, n1 % n2);
	}

	/*

	private static int getMod(int number, int mod) {
	while (number < 0)
		number += mod;
	return number % mod;
	}

	public static int startingMethod(int e, int phi) {
		// Extended Euclidean Algorithm  Tip: https://www.delftstack.com/howto/java/mod-of-negative-numbers-in-java/#get-the-mod-of-negative-numbers-by-using-the-operator-in-java

		int[] p = {0,1}; // for the first two iterations


		// first iteration: p_2
		int dividend = phi; // always the provided phi
		int divisor = e; // begins with e, but changes at each iteration

		int quotient = dividend / divisor; // integer division - initialize the first step
		int remainder = dividend % divisor;

		int tmp_p = p[0] - p[1] * quotient;

		// mod of a possibly negative number
		int p_i = getMod(tmp_p, phi);

		if(remainder == 1)
			return p_i;

		// iteration p_3
		dividend = divisor;
		divisor = remainder;

		quotient = dividend / divisor;
		remainder = dividend % divisor;

		tmp_p = p[1] - p_i * quotient;

		p_i = getMod(tmp_p, phi);

		while(remainder != 1) {
			// performs the next iteration
			dividend = divisor;
			divisor = remainder;

			quotient = dividend / divisor;
			remainder = dividend % divisor;

			tmp_p = p[1] - p_i * quotient;
			p_i = getMod(tmp_p, phi);
		}

		return p_i;
	}

	 */
}