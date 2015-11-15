package yong.dealer.chat.client;
public interface Constants {
// an interface variable is implicitly static and final and obviously public
    /**
     App server
     */
	String SERVER_URL = "http://1-dot-mylocaltrader2015.appspot.com";

    /**
     * Google API project id registered to use GCM.
     */
     String SENDER_ID = "192156478808";
    /*
    Log in variables
     */
    String LOGININFO ="login_information";
    String LOGINUSERNAME = "login_username";
    String LOGINEMAIL    ="login_email";
    String LOGINPASSWORD ="login_password";
    String LOGINREGID    ="login_registerid"; // this varibale is unique to android device and sender_id
}
