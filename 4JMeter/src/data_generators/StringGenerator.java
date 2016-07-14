package data_generators;


import java.util.Random;

public abstract class StringGenerator {

	private static final String letterBank = "abcdefghijklmnopqrstuvwxyz1234567890";
	
	public static String getRandomGeneratedString(int length) {
		
		char[] chars = new char[length];
		
		for(int i=0;i<chars.length;i++)
		{
			int radix = Math.abs(new Random().nextInt()%letterBank.length());			
			chars[i] = letterBank.charAt(radix);			
		}
		
		return new String(chars);
		
	}
}
