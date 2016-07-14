package tests;
import infrastructure.TestCase;

import java.io.IOException;

import EventObjects.EventType;
import exceptions.TestFailedException;

public class RegisterTest extends TestCase {	 
		
	@Override
	public void runTest() throws IOException, InterruptedException, TestFailedException {					
		_serverProxy.prepareSmsCode(_userA.getId());
		assertResult(EventType.GET_SMS_CODE_SUCCESS);
		
		_sampleResult.sampleStart();
		_serverProxy.register(_userA.getId(), _userA.getToken());
		assertResult(EventType.REGISTER_SUCCESS);	
	}
	
	@Override
	protected void testCleanUp() {	 	
		  	
	  	try {
			_serverProxy.deleteUser(_userA.getId());
			waitWithoutAssert();
			
		} catch (Exception e) {				
			_logger.warning("testCleanUp() failed. Exception:"+(e.getMessage()!=null ? e.getMessage() : e));
			
		}	  
	 }	 
}