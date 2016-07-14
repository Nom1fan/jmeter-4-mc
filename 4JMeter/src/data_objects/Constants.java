package data_objects;

import ServerObjects.ILangStrings;

public abstract class Constants {

	public static final String  KEY_LOGIC_SERVER_HOST 	= 	"LogicServerHost";
	public static final String KEY_LOGIC_SERVER_PORT 	= 	"LogicServerPort";
	public static final String KEY_STORAGE_SERVER_HOST	= 	"StorageServerHost";
	public static final String KEY_STORAGE_SERVER_PORT 	= 	"StorageServerPort";	 
	public static final String TEST_DEVICE_MODEL 		= 	"TEST_DEVICE_MODEL";	
	public static final String TEST_DEST_NAME 			=  	"TEST_DEST_NAME";
	public static final String TEST_ANDROID_VERSION		=	"6.0.1";
	public static final String TEST_DEVICE_NUMBER  		=	"0500000000";
	
	public static final String TEST_LOCALE				= 	ILangStrings.ENGLISH;
	
	public static final double TEST_APP_VERSION			=	1.18;
	
	public static final int TEST_SMS_CODE				=	1111;
	
	public static final int WAIT_FOR_RESULT_TIMEOUT 	= 	60*1000; //	Milliseconds	

	
	/* DB Constants */
	
    // Table users
    public static final String TABLE_USERS              =   "users";
    // Table keys
    public static final String COL_UID                  =   "uid";
    public static final String COL_TOKEN                =   "token";
    public static final String COL_REGISTERED_DATE      =   "registered_date";
    public static final String COL_USER_STATUS          =   "user_status";
    public static final String COL_UNREGISTERED_DATE    =   "unregistered_date";
    public static final String COL_UNREGISTERED_COUNT   =   "unregistered_count";
    public static final String COL_DEVICE_MODEL         =   "device_model";
    public static final String COL_ANDROID_VERSION      =   "android_version";
    
    

    // Tables media_transfers, media_calls and media_files
    public static final String TABLE_MEDIA_TRANSFERS    =   "media_transfers";
    // Table media_transfers keys
    public static final String COL_TRANSFER_ID          =   "transfer_id";
    public static final String COL_TRANSFER_SUCCESS     =   "transfer_success";
    public static final String COL_TRANSFER_DATETIME    =   "transfer_datetime";
    

    public static final String TABLE_MEDIA_CALLS        =   "media_calls";
    // Table media_calls keys
    public static final String COL_CALL_ID              =   "call_id";
    public static final String COL_MD5_VISUAL           =   "md5_visual";
    public static final String COL_MD5_AUDIO            =   "md5_audio";
    

    public static final String TABLE_MEDIA_FILES        =   "media_files";
    // Table media_files keys
    public static final String COL_CONTENT_EXTENSION    =   "content_ext";
    public static final String COL_CONTENT_SIZE         =   "content_size";
    public static final String COL_TRANSFER_COUNT       =   "transfer_count";
    public static final String COL_CALL_COUNT           =   "call_count";

    

    // Shared keys
    public static final String COL_TYPE                 =   "type";     // Used in media_transfers and media calls
    public static final String COL_UID_SRC              =   "uid_src";  // PK in users. FK used in media transfers and media calls
    public static final String COL_UID_DEST             =   "uid_dest"; // PK in users. FK used in media transfers and media calls
    public static final String COL_MD5                  =   "md5";      // PK in media_files. Used in media transfers as well.
    
    

    // Table app_meta
    public static final String TABLE_APP_META           =   "app_meta";

    // Table keys
    public static final String COL_CURRENT_VERSION      =   "current_version";
    public static final String COL_LAST_SUPPORTED_VER   =   "last_supported_version";
    
    

    // Table sms_verification
    public static final String TABLE_SMS_VERIFICATION   =   "sms_verification";
    // Table keys
    // COL_UID also used here
    public static final String COL_CODE                 =   "code";
}
