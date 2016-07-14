package proxies;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import ClientObjects.ConnectionToServer;
import ClientObjects.ServerProxy;
import DataObjects.DataKeys;
import DataObjects.SpecialMediaType;
import EventObjects.EventReport;
import FilesManager.FileManager;
import LogObjects.LogsManager;
import MessagesToClient.MessageToClient;
import MessagesToServer.MessageToServer;
import MessagesToServer.ServerActionType;
import actions.ActionFactory;
import actions.ClientAction;
import data_objects.Constants;


public class SimpleServerProxy implements ServerProxy {

	private Object _lock = new Object();
	private EventReport _lastEventReport = null;
	private boolean _locked;
	private static Logger _logger;
	private String _logicServerHost;
	private int _logicServerPort;
	private String _storageServerHost;
	private int _StorageServerPort;
	private List<ConnectionToServer> conns;

	static 
	{
		prepareLogger();
	}

	public SimpleServerProxy() throws Exception {
		conns = new LinkedList<>();
		
		Properties prop = new Properties();
		InputStream input = new FileInputStream("resources\\config.properties");
		prop.load(input);
		System.out.println("Server host=" + prop.getProperty("server.host"));
		System.out.println("Server logic port=" + prop.getProperty("server.logic.port"));
		System.out.println("Server storage port=" + prop.getProperty("server.storage.port"));

		_logicServerHost = _storageServerHost = prop.getProperty("server.host");
		_logicServerPort = Integer.parseInt(prop.getProperty("server.logic.port"));
		_StorageServerPort = Integer.parseInt(prop.getProperty("server.storage.port"));
	}
	
	public SimpleServerProxy(	String logicServerHost, 
			int LogicServerPort, 
			String storageServerHost, 
			int StorageServerPort) throws IOException {

		_logicServerHost = logicServerHost;
		_logicServerPort = LogicServerPort;
		_storageServerHost = storageServerHost;
		_StorageServerPort = StorageServerPort;	
		conns = new LinkedList<>();
	}	

	@Override
	public ConnectionToServer openConnection(String host, int port) throws IOException {
//		_logger.info("Connecting to address:" + host + ":" + port);
		ConnectionToServer conn = new ConnectionToServer(host, port, this);
		conn.openConnection();
		conns.add(conn);
		return conn;
	}

	@Override
	public void register(String userId, String userToken) throws IOException {

		_logger.info("Registering [User]:" + userId + ". [Token]:" + userToken);
		
		HashMap<DataKeys, Object> data = new HashMap<>();
		data.put(DataKeys.DEVICE_MODEL, Constants.TEST_DEVICE_MODEL);
		data.put(DataKeys.ANDROID_VERSION, Constants.TEST_ANDROID_VERSION);
		data.put(DataKeys.PUSH_TOKEN, userToken);
		data.put(DataKeys.SMS_CODE, Constants.TEST_SMS_CODE);

		MessageToServer msgRegister = new MessageToServer(ServerActionType.REGISTER, userId, data);
		ConnectionToServer conn = openConnection(_logicServerHost, _logicServerPort);
		conn.sendToServer(msgRegister);
	}

	@Override
	public void uploadFile(String srcId, String destId, SpecialMediaType specialMediaType ,FileManager fileForUpload) throws IOException {
		_logger.info("Uploading file from [User]:" + srcId + " to [User]:" + destId);
		
		HashMap<DataKeys, Object> data = new HashMap<>();

		data.put(DataKeys.APP_VERSION, Constants.TEST_APP_VERSION);
		data.put(DataKeys.SOURCE_ID, srcId);
		data.put(DataKeys.SOURCE_LOCALE, Constants.TEST_LOCALE);
		data.put(DataKeys.DESTINATION_ID, destId);
		data.put(DataKeys.DESTINATION_CONTACT_NAME, Constants.TEST_DEST_NAME);
		data.put(DataKeys.MANAGED_FILE, fileForUpload);
		data.put(DataKeys.MD5, fileForUpload.getMd5());
		data.put(DataKeys.EXTENSION, fileForUpload.getFileExtension());
		data.put(DataKeys.FILE_PATH_ON_SRC_SD, fileForUpload.getFileFullPath());
		data.put(DataKeys.FILE_SIZE, fileForUpload.getFileSize());
		data.put(DataKeys.FILE_TYPE, fileForUpload.getFileType());
		data.put(DataKeys.SPECIAL_MEDIA_TYPE, specialMediaType);
		data.put(DataKeys.SOURCE_WITH_EXTENSION, srcId + "." + fileForUpload.getFileExtension());


		ConnectionToServer conn = openConnection(_storageServerHost, _StorageServerPort);			
		DataOutputStream dos = null;
		BufferedInputStream bis = null;
		try {
			MessageToServer msgUF = new MessageToServer(ServerActionType.UPLOAD_FILE, srcId, data);
			conn.sendToServer(msgUF);

			_logger.info("Initiating file data upload...");

			dos = new DataOutputStream(conn.getClientSocket().getOutputStream());

			FileInputStream fis = new FileInputStream(fileForUpload.getFile());
			bis = new BufferedInputStream(fis);

			byte[] buf = new byte[1024 * 8];
			long fileSize = fileForUpload.getFileSize();
			long bytesToRead = fileSize;
			int bytesRead;
			while (bytesToRead > 0 && (bytesRead = bis.read(buf, 0, (int) Math.min(buf.length, bytesToRead))) != -1) {
				dos.write(buf, 0, bytesRead);
				//String msg = "File upload to:"+td.getDestinationId()+" %.0f%% complete";
				//float percent = (float) (fileSize - bytesToRead) / fileSize * 100;
				//publishProgress(String.format(msg, percent));
				bytesToRead -= bytesRead;
			}
		}
		finally {            
			try {
				if(bis!=null) {
					bis.close();
				}
				conn.closeConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void prepareSmsCode(String userId) throws IOException {
		
		_logger.info("Preparing SMS code for [User]:" + userId);					

		ConnectionToServer conn = openConnection(_logicServerHost, _logicServerPort);
		MessageToServer msgGetSms = new MessageToServer(ServerActionType.GET_SMS_CODE_FOR_LOAD_TEST, userId);
		conn.sendToServer(msgGetSms);
	}

	@Override
	public void unregister(String userId, String userToken) throws IOException {

		_logger.info("Unregistering [User]:" + userId);			
		
		HashMap<DataKeys,Object> data = new HashMap<>();		
		data.put(DataKeys.PUSH_TOKEN, userToken);
		
		MessageToServer msgUnregister = new MessageToServer(ServerActionType.UNREGISTER, userId, data);
		ConnectionToServer conn = openConnection(_logicServerHost, _logicServerPort);
		conn.sendToServer(msgUnregister);
	}

	@Override
	public void deleteUser(String userId) throws IOException {
		
		_logger.info("Deleting [User]:" + userId);		
				
		HashMap<DataKeys,Object> data = new HashMap<>();				
		data.put(DataKeys.TABLE, Constants.TABLE_SMS_VERIFICATION);
		data.put(DataKeys.COLUMN, Constants.COL_UID);
		data.put(DataKeys.CONDITION, "=");
		data.put(DataKeys.VALUE, userId);
		
		MessageToServer msgDelUser = new MessageToServer(ServerActionType.DELETE_FROM_DB, userId ,data);
		ConnectionToServer conn = openConnection(_logicServerHost, _logicServerPort);
		conn.sendToServer(msgDelUser);
		
		data = new HashMap<>();
		data.put(DataKeys.TABLE, Constants.TABLE_USERS);
		data.put(DataKeys.COLUMN, Constants.COL_UID);
		data.put(DataKeys.CONDITION, "=");
		data.put(DataKeys.VALUE, userId);
		
		msgDelUser = new MessageToServer(ServerActionType.DELETE_FROM_DB, userId ,data);
		conn = openConnection(_logicServerHost, _logicServerPort);
		conn.sendToServer(msgDelUser);	
	}
		
	@Override
	public void handleMessageFromServer(MessageToClient msg, ConnectionToServer connectionToServer) {

		try
		{
			_lastEventReport = null;			  
			_logger.info("[MessageToClient]:"+msg.toString());

			ClientAction clientAction = ActionFactory.instance().getAction(msg.getActionType());
			clientAction.setConnectionToServer(connectionToServer);
			_lastEventReport = clientAction.doClientAction(msg.getData());

			if(_lastEventReport!=null)
				_logger.info("Performed clientAction. [EventReport]:" + _lastEventReport.status());
		} catch(Exception e) {

			_logger.severe("Failed to process [MessageToClient]:" + msg.toString() + 
					". [Exception]:" + e.getMessage()!=null ? e.getMessage() : e.toString());

		} finally {

			synchronized(_lock) {
				_locked = false;
				_lock.notify();
			}
			closeConnection(connectionToServer);
		}
	}

	@Override
	public void isRegistered(String srcId, String destId) throws IOException {
		
		HashMap<DataKeys, Object> data = new HashMap<>();
		data.put(DataKeys.DESTINATION_ID, destId);
		
		MessageToServer msgIsLogin = new MessageToServer(ServerActionType.IS_REGISTERED, srcId, data);
		ConnectionToServer conn = openConnection(_logicServerHost, _logicServerPort);
		conn.sendToServer(msgIsLogin);
	}
	
	@Override
	public void closeConnection(ConnectionToServer connectionToServer) {
		
		conns.remove(connectionToServer);
		try {
			connectionToServer.closeConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Override
	public EventReport getEventReport() throws InterruptedException {

		_locked = true;

		synchronized(_lock) {

			while(_locked) {
				_logger.info("getEventReport waiting for notify...");
				_lock.wait(Constants.WAIT_FOR_RESULT_TIMEOUT);
				_locked = false; // Timeout exceeded. Shouldn't stay stuck here.
			}
		}

		return _lastEventReport;
	}

	private static void prepareLogger() {

		String logPath = 
				Paths.get("").toAbsolutePath().toString() + 
				"\\TestLogs\\";

		LogsManager.clearLogs(logPath);
		_logger = LogsManager.getLogger(
				"MediaCallzLoadTests" , 
				logPath);

	}
		
	@Override
	public void handleDisconnection(ConnectionToServer cts, String msg) {
		_logger.severe(msg);
		try {
			cts.closeConnection();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	
	public static void main(String args[]) throws Exception {
		
		new SimpleServerProxy();
	}


}
