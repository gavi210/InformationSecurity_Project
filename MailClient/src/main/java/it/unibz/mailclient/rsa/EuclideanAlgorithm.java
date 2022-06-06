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
}