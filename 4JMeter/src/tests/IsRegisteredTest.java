package tests;
import infrastructure.TestCase;

import java.io.IOException;

import data_objects.Constants;
import EventObjects.EventType;
import exceptions.TestFailedException;

public class IsRegisteredTest extends TestCase {	 
		
	@Override
	public void runTest() throws IOException, InterruptedException, TestFailedException {			
		_serverProxy.prepareSmsCode(_userA.getId());
		assertResult(EventType.GET_SMS_CODE_SUCCESS);
				
		_serverProxy.register(_userA.getId(), _userA.getToken());
		assertResult(EventType.REGISTER_SUCCESS);
		
		_sampleResult.sampleStart();
		_serverProxy.isRegistered(_userA.getId(), Constants.TEST_DEVICE_NUMBER);
		assertResult(EventType.USER_REGISTERED_TRUE);
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