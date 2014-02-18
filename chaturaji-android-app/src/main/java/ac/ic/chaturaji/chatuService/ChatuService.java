package ac.ic.chaturaji.chatuService;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haider on 06/02/14.
 */
public class ChatuService{

    private String localHost = ""; // Put your server address here...
    private HttpClient httpClient = new DefaultHttpClient();

    public String getGames(){

        String games = "Error";
        String url = "http://" + localHost + ":8080/chaturaji-web-services/games";


        try{
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            games = org.apache.http.util.EntityUtils.toString(entity);

        }

        catch (Exception e){

        e.printStackTrace();

        }

        return games;
    }

    public String createGame(String AIOpps){

        String url = "http://" + localHost + ":8080/chaturaji-web-services/game";

        try{
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("numberOfAIPlayers", "3"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            Log.d("Http Response:", response.toString());
        }

        catch (Exception e){

            e.printStackTrace();
            return "Error";

        }
        return "Success";
    }

}
