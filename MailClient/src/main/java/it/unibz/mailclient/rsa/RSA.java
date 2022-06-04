package it.unibz.mailclient.rsa;

import java.math.BigInteger;
import java.util.Random;

public class RSA {

	private static final int ASCII_TABLE_LENGTH = 128;
	private static final int SHIFT = 3;

	/**
	 * there is an important condition: each input character must be within a valid range of values. In particular, each input character
	 * must be smaller than n.
	 * With n too big, the computation of the algorithm is quite extensive and slow, therefore, we decided to restrict the
	 * input characters that are allowed for an email to be encrypted.
	 * We'll reduce the character to only those in the ASCII table:
	 * as mapping, all characters are in reverse order
	 * @return
	 */
	public RSAKeyPair generateKeys() {

		// generate two random prime numbers p and q. Tip: https://stackoverflow.com/questions/24006143/generating-a-random-prime-number-in-java
		int lowerBound = 2; // cannot be 1, since n-1 = 0 and phi = 0. Maybe an edge case
		int upperBound = 100;

		int[] startingValues = getStartingPrimes(lowerBound, upperBound);
		int p = startingValues[0];
		int q = startingValues[1];


		// calculate n = p * q
		int n = p * q;

		// calculate phi = (p-1)*(q-1)
		int phi = (p - 1) * (q - 1);

		int e = getCoprime(phi);

		int d = EuclideanAlgorithm.getPrivateKeyValue(e, phi);

		/*
		System.out.println("P: " + p);
		System.out.println("Q: " + q);
		System.out.println("N: " + n);
		System.out.println("Phi: " + phi);

		System.out.println("D: " + d);
		System.out.println("e: " + e);
		*/

		return new RSAKeyPair(n, d, e);
	}

	public int[] encrypt(String plaintext, int e, int n){

		int[] alphabet_positions = new int[plaintext.length()];
		int i = 0;

		for (char c : plaintext.toCharArray()) {
			// alphabet_positions[i] = (int) c - (int) 'a' + 1;
			alphabet_positions[i] = c + SHIFT;
			i++;
		}

		//for each number from the plaintext compute  ( pow(number, e) ) mod n
		int[] ciphertext = new int[plaintext.length()];
		for (i = 0; i < plaintext.length(); i++) {
			BigInteger tmp = BigInteger.valueOf(alphabet_positions[i]);
			BigInteger tmp_pow = tmp.pow(e);

			ciphertext[i] = tmp_pow.mod(BigInteger.valueOf(n)).intValue();
		}

		return ciphertext;
	}

	public String decrypt(int[] ciphertext, int d, int n){

		// for each number in the ciphertext compute ( pow(number, d) ) mod n
		int[] plainText = new int[ciphertext.length];

		for(int i = 0; i < ciphertext.length; i++) {
			BigInteger tmp = BigInteger.valueOf(ciphertext[i]);
			BigInteger tmp_pow = tmp.pow(d);

			plainText[i] = tmp_pow.mod(BigInteger.valueOf(n)).intValue();
		}

		//each resulting number is converted into a character assuming that this number is the position of the character in the alphabet
		char[] chars = new char[plainText.length];
		for(int i = 0; i < ciphertext.length; i++) {
			// chars[i] = (char) (plainText[i] + (int) 'a' - 1);
			chars[i] = (char) (plainText[i] - SHIFT);
		}

		return new String(chars);
	}


	private int[] getStartingPrimes(int lowerBound, int upperBound) {
		int p = generatePrimeNumber(lowerBound, upperBound);
		int q = generatePrimeNumber(lowerBound, upperBound);

		while(p == q || p * q < (ASCII_TABLE_LENGTH + SHIFT)) {
			p = generatePrimeNumber(lowerBound, upperBound);
			q = generatePrimeNumber(lowerBound, upperBound);
		}

		return new int[] {p, q};
	}

	private int generatePrimeNumber(int lowerBound, int upperBound) {
		int delta = upperBound - lowerBound;
		Random rand = new Random(); // generate a random number
		int num = rand.nextInt(delta) + lowerBound;

		while (!isPrime(num)) {
			num = rand.nextInt(delta) + lowerBound;
		}
		return num;
	}

	private boolean isPrime(int inputNum){
		if (inputNum <= 3 || inputNum % 2 == 0)
			return inputNum == 2 || inputNum == 3; //this returns false if number is <=1 & true if number = 2 or 3
		int divisor = 3;
		while ((divisor <= Math.sqrt(inputNum)) && (inputNum % divisor != 0))
			divisor += 2; //iterates through all possible divisors
		return inputNum % divisor != 0; //returns true/false
	}

	private int getCoprime(int phi) {
		int i = 2;
		while(i < phi && EuclideanAlgorithm.gcdEuclidean(i,phi) != 1)
			i++;

		return i == phi ? -1 : i;
	}
}