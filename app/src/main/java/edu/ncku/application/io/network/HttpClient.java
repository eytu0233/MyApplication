package edu.ncku.application.io.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by NCKU on 2016/2/23.
 */
public class HttpClient {

    /**
     * 向指定URL發送POST方法的請求
     * @param urlStr   發送請求的URL
     * @param parmas  請求参數，請求参數應該是name1=value1&name2=value2的形式
     * @return URL所代表遠程資源的回應
     */
    public static String sendPost(String urlStr, String parmas) throws IOException {

        String result = "";
        BufferedReader bufferedReader = null;
        DataOutputStream dos = null;

        try {
            URL url = new URL(urlStr);

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();
                urlConnection.setRequestMethod("POST");

                // Send post request
                urlConnection.setDoOutput(true);
                dos = new DataOutputStream(
                        urlConnection.getOutputStream());

                // Set parameters ID and Password with url encode format
                dos.writeBytes(parmas);

                dos.flush();
                dos.close();

                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                for (; (line = bufferedReader.readLine()) != null; ) {
                    result += "\n" + line;
                }
            }catch (IOException e) {
                // TODO Auto-generated catch block
                throw e;
            }
        }  catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (null != bufferedReader) bufferedReader.close();
                if (null != dos) dos.close();
            } catch (IOException e) {
                throw e;
            }
        }

        return result;
    }

}
