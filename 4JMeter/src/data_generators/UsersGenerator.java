package data_generators;

import data_objects.TestUser;

public abstract class UsersGenerator {

	public static TestUser generateUser() {
		
		String id = StringGenerator.getRandomGeneratedString(10);
		String token = StringGenerator.getRandomGeneratedString(40);
		return new TestUser(id,token);
	}
}
