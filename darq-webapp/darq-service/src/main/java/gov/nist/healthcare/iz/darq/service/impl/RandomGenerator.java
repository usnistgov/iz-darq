package gov.nist.healthcare.iz.darq.service.impl;

import java.security.SecureRandom;

public class RandomGenerator {
	public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String lower = upper.toLowerCase();
	public static final String digits = "0123456789";
	public static final String alphaNumeric = upper + lower + digits;
	private final char[] symbols = alphaNumeric.toCharArray();
	private final SecureRandom random = new SecureRandom();

	public String generate(int length) {
		char[] buffer = new char[length];
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = symbols[random.nextInt(symbols.length)];
		}
		return new String(buffer);
	}
}
