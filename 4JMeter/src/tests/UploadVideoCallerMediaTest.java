package tests;

import infrastructure.TestCase;

import java.io.File;
import java.io.IOException;

import DataObjects.SpecialMediaType;
import EventObjects.EventType;
import Exceptions.FileDoesNotExistException;
import Exceptions.FileExceedsMaxSizeException;
import Exceptions.FileInvalidFormatException;
import Exceptions.FileMissingExtensionException;
import FilesManager.FileManager;
import FilesManager.FileManager.FileType;
import data_generators.ResourceGenerator;
import data_generators.UsersGenerator;
import data_objects.TestUser;
import exceptions.TestFailedException;

public class UploadVideoCallerMediaTest extends TestCase {

	private TestUser _userB;

	@Override
	public void runTest() throws IOException, InterruptedException,
			TestFailedException {

		_userB = UsersGenerator.generateUser();

		_serverProxy.prepareSmsCode(_userA.getId());
		assertResult(EventType.GET_SMS_CODE_SUCCESS);
				
		_serverProxy.register(_userA.getId(), _userA.getToken());
		assertResult(EventType.REGISTER_SUCCESS);

		_serverProxy.prepareSmsCode(_userB.getId());
		assertResult(EventType.GET_SMS_CODE_SUCCESS);
		
		_serverProxy.register(_userB.getId(), _userB.getToken());
		assertResult(EventType.REGISTER_SUCCESS);
		
		File videoFile = ResourceGenerator.getInstance().generateResource(
				FileType.VIDEO);
		FileManager fileForUpload;

		try {
			fileForUpload = new FileManager(videoFile);
		} catch (NullPointerException | FileInvalidFormatException
				| FileExceedsMaxSizeException | FileDoesNotExistException
				| FileMissingExtensionException e) {

			String errMsg = e.getMessage() != null ? e.getMessage() : e
					.toString();
			throw new TestFailedException(errMsg);
		}
		_sampleResult.sampleStart();
		_serverProxy.uploadFile(_userA.getId(), _userB.getId(),
				SpecialMediaType.CALLER_MEDIA, fileForUpload);			
	}

	@Override
	protected void testCleanUp() {

		try {
			// TODO Implement media clear
			// TODO implement communication history clear

			_serverProxy.deleteUser(_userA.getId());
			waitWithoutAssert();
			_serverProxy.deleteUser(_userB.getId());
			waitWithoutAssert();

		} catch (Exception e) {
			_logger.warning("testCleanUp() failed. Exception:"
					+ (e.getMessage() != null ? e.getMessage() : e));

		}

	}

}