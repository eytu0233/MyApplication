package edu.ncku.application.io;

/**
 *為了提高聚合性，使用這個介面來儲存常數值，可以避免一邊有改而一邊沒改的問題
 * 所以file跟network這兩個故package裡，會實作這個類別來取得常數
 */
public interface IOConstatnt {

    String UPCOMING_EVENT_FILE = "NCKU_Lib_Upcoming_Event";
    String UPCOMING_EVENT_URL = "http://m.lib.ncku.edu.tw/libweb/index.php?item=webActivity&lan=";

    String CONTACT_FILE = "NCKU_Lib_Contact_Info";
    String CONTACT_URL = "http://m.lib.ncku.edu.tw/libweb/index.php?item=webOrganization&lan=";

    String FLOOR_INFO_FILE = "NCKU_Lib_Floor_Info";
    String FLOOR_INFO_URL = "http://m.lib.ncku.edu.tw/libweb/index.php?item=webFloorplan&lan=";

    String LIB_OPEN_TIME_FILE = "NCKU_Lib_Open_Time";
    String LIB_OPEN_TIME_URL = "http://m.lib.ncku.edu.tw/libweb/libInfoJson.php";

    String NEWS_FILE = "News";
    String NEWS_URL = "http://m.lib.ncku.edu.tw/libweb/index.php?item=webNews&lan=";
}
