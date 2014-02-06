package ac.ic.chaturaji.chatuService;

import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Haider on 06/02/14.
 */
public class ChatuService{

    public String getGames(){

        String localHost = ""; // Enter the IP address of your server here!!!
        String games = "";
        String url = "http://" + localHost + ":8080/chaturaji-web-services/games";
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        try{

            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            games = org.apache.http.util.EntityUtils.toString(entity);

        } catch (ClientProtocolException e){

        e.printStackTrace();

        } catch (IOException e){

        e.printStackTrace();
        }

        return games;
    }

}
