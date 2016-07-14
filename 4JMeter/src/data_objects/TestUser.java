package data_objects;

public class TestUser {
	
	private String _id;
	private String _token;
	
	public TestUser(String id, String token) {
		
		_id = id;
		_token = token;
	}

	public String getToken() {
		return _token;
	}
	
	public String getId() {
		return _id;
	}
	
}
