package ac.ic.chaturaji.chatuService;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haider on 06/02/14.
 */
public class ChatuService{

    private String localHost = "192.168.2.2"; // Put your server address here...
    private HttpClient httpClient;

    public ChatuService(){

        // Acknowledgment to http://madurangasblogs.blogspot.co.uk/2013/08/avoiding-javaxnetsslsslpeerunverifiedex.html

        try{
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            httpClient = new DefaultHttpClient(ccm, params);

            }

        catch (Exception e) {
            httpClient = new DefaultHttpClient();

        }
    }

    public String getGames(){

        String games = "Error";
        String url = "https://" + localHost + ":8443/chaturaji-web-services/games";


        try{
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            games = org.apache.http.util.EntityUtils.toString(entity);
            System.out.println(games);

        }

        catch (Exception e){

        e.printStackTrace();

        }


        return games;
    }

    public String createGame(String AIOpps){

        String url = "https://" + localHost + ":8443/chaturaji-web-services/game";

        try{
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("numberOfAIPlayers", "3"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            Log.d("Http Response:", Integer.toString(response.getStatusLine().getStatusCode()));
            if(response.getStatusLine().getStatusCode() == 401)
                return "Error";
        }

        catch (Exception e){

            e.printStackTrace();
            return "Error";

        }

        return "Success";
    }

    public String createAccount(String email, String password, String nickname){

        String url = "https://" + localHost + ":8443/chaturaji-web-services/register";

        try{

            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            nameValuePairs.add(new BasicNameValuePair("nickname", nickname));

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
