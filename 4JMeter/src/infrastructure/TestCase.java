package infrastructure;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import proxies.SimpleServerProxy;
import ClientObjects.ServerProxy;
import DataObjects.SharedConstants;
import EventObjects.EventReport;
import EventObjects.EventType;
import LogObjects.LogsManager;
import data_generators.UsersGenerator;
import data_objects.Constants;
import data_objects.TestUser;
import exceptions.TestFailedException;

public abstract class TestCase extends AbstractJavaSamplerClient {

	private EventType _nextExpectedEvent;
	protected SampleResult _sampleResult;
	protected static Logger _logger;
	protected TestUser _userA;
	protected ServerProxy _serverProxy;

	static {
		prepareLogger();
	}

	/**
	 * Sets up default arguments for the JMeter GUI
	 */
	@Override
	public Arguments getDefaultParameters() {
		Arguments defaultParameters = new Arguments();
		defaultParameters.addArgument(Constants.KEY_LOGIC_SERVER_HOST,
				SharedConstants.LOGIC_SERVER_HOST);
		defaultParameters.addArgument(Constants.KEY_LOGIC_SERVER_PORT,
				new Integer(SharedConstants.LOGIC_SERVER_PORT).toString());
		defaultParameters.addArgument(Constants.KEY_STORAGE_SERVER_HOST,
				SharedConstants.STROAGE_SERVER_HOST);
		defaultParameters.addArgument(Constants.KEY_STORAGE_SERVER_PORT,
				new Integer(SharedConstants.STORAGE_SERVER_PORT).toString());
		return defaultParameters;
	}

	/**
	 * Implement by subclass test case. Subclasses must call
	 * initializeTestMembers() as first line of implementation.
	 */
	@Override
	public SampleResult runTest(JavaSamplerContext ctx) {

		// JMeterVariables vars =
		// JMeterContextService.getContext().getVariables();
		// vars.put("demo", "demoVariableContent"); <--- These variables can be
		// used to pass values between tests!

		try {
			initializeTestMembers(ctx);

			_logger.info("Starting test run");
			runTest();

		} catch (InterruptedException | IOException e) {
			String errMsg = "Error occured:"
					+ (e.getMessage() != null ? e.getMessage() : e);
			_logger.severe(errMsg);
			return failed(errMsg);
		} catch (TestFailedException e) {
			String errMsg = (e.getMessage() != null ? e.getMessage() : e
					.toString());
			_logger.severe(errMsg);
			return failed(errMsg);
		} finally {
			_testCleanUp();
		}

		passed(_nextExpectedEvent.toString());
		return _sampleResult;
	}

	/**
	 * Test body to be implemented by tests
	 * 
	 * @throws InterruptedException
	 * @throws TestFailedException
	 * @throws IOException
	 */
	protected abstract void runTest() throws InterruptedException,
			TestFailedException, IOException;

	/**
	 * Test cleanup to be implemented by tests
	 */
	protected abstract void testCleanUp();

	/**
	 * Asserting the result received from server.
	 * 
	 * @param expectedResult
	 *            The result expected to be received from server.
	 * @throws TestFailedException
	 *             Thrown in case the result received is not as expected.
	 * @throws InterruptedException
	 *             Thrown if thread was interrupted while waiting for result.
	 */
	protected void assertResult(EventType expectedResult)
			throws TestFailedException, InterruptedException {

		_nextExpectedEvent = expectedResult;
		EventReport actualResult = _serverProxy.getEventReport();

		if (actualResult == null
				|| !actualResult.status().equals(_nextExpectedEvent))
			throw new TestFailedException("Test failed. [Expected result]:"
					+ _nextExpectedEvent + ". [Actual result]:"
					+ actualResult.status());
	}

	/**
	 * Waits for result from server without asserting the result. Should be used
	 * when performing cleanUp actions.
	 * 
	 * @throws InterruptedException
	 *             Thrown if thread was interrupted while waiting for result
	 */
	protected void waitWithoutAssert() throws InterruptedException {

		_serverProxy.getEventReport();
	}

	/**
	 * Declares the test as passed. Showing the successResponseMessage to be
	 * displayed on JMeter.
	 * 
	 * @param successResponseMessage
	 * @return
	 */
	protected SampleResult passed(String successResponseMessage) {

		_sampleResult.sampleEnd();
		_sampleResult.setSuccessful(true);
		_sampleResult.setResponseMessage(successResponseMessage);
		_sampleResult.setResponseCodeOK();
		_logger.info("Test passed.");
		return _sampleResult;
	}

	/**
	 * Declares the test as failed. Showing the failureResponseMessage to be
	 * displayed on JMeter.
	 * 
	 * @param failureResponseMessage
	 * @return
	 */
	protected SampleResult failed(String failureResponseMessage) {

		_sampleResult.sampleEnd();
		_sampleResult.setSuccessful(false);
		_sampleResult.setResponseMessage(failureResponseMessage);
		_logger.severe("Test failed.");
		return _sampleResult;
	}

	/**
	 * Initializes all parameters for the test including a single generated
	 * user. (Minimum requirement for any test). Additional users (if needed)
	 * should be generated by the test. Host and port are retrieved from JMeter
	 * configuration.
	 * 
	 * @see UsersGenerator
	 * @see JavaSamplerContext
	 * @param ctx
	 *            The context supplied by JMeter
	 * @throws IOException
	 */
	protected void initializeTestMembers(JavaSamplerContext ctx)
			throws IOException {

		_logger.info("Initializing members...");
		_sampleResult = new SampleResult();
		_userA = UsersGenerator.generateUser();
		_serverProxy = new SimpleServerProxy(
				ctx.getParameter(Constants.KEY_LOGIC_SERVER_HOST),
				ctx.getIntParameter(Constants.KEY_LOGIC_SERVER_PORT),
				ctx.getParameter(Constants.KEY_STORAGE_SERVER_HOST),
				ctx.getIntParameter(Constants.KEY_STORAGE_SERVER_PORT));

	}

	private static void prepareLogger() {

		String logPath = Paths.get("").toAbsolutePath().toString()
				+ "\\TestLogs\\";

		LogsManager.clearLogs(logPath);
		_logger = LogsManager.getLogger("MediaCallzLoadTests", logPath);

	}

	/**
	 * Wrapper method called when the test is finished. Invoking
	 * test-implemented cleanUp.
	 */
	private void _testCleanUp() {

		_logger.info("Performing cleanUp...");
		testCleanUp();
		_logger.info("CleanUp complete.");
	}

}
