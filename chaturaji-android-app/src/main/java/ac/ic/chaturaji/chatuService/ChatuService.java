package ac.ic.chaturaji.chatuService;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Haider on 06/02/14.
 */
public class ChatuService{

    private String localHost = "192.168.2.2"; // Put your server address here...
    private HttpClient httpClient;
    private String email = "";
    private String password = "";

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

            CredentialsProvider credsProvider = new BasicCredentialsProvider();

            HttpClientContext context = HttpClientContext.create();

            credsProvider.setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(email, password));

            context.setCredentialsProvider(credsProvider);

            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("numberOfAIPlayers", AIOpps));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost, context);

            System.out.println(response.getStatusLine().getStatusCode());

            if(response.getStatusLine().getStatusCode() == 401)
                return "Error";
        }

        catch (Exception e){

            e.printStackTrace();
            return "Error";

        }

        return "Success";
    }

    public String joinGame(String gameId){

        String url = "https://" + localHost + ":8443/chaturaji-web-services/joinGame";

        try{

            CredentialsProvider credsProvider = new BasicCredentialsProvider();

            HttpClientContext context = HttpClientContext.create();

            credsProvider.setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(email, password));

            context.setCredentialsProvider(credsProvider);

            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("gameId", gameId));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost, context);

            System.out.println(response.getStatusLine().getStatusCode());

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

            httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                    new BasicNameValuePair("email", email),
                    new BasicNameValuePair("password", password),
                    new BasicNameValuePair("nickname", nickname)
            )));

            HttpResponse response = httpClient.execute(httpPost);

            System.out.println(response.getStatusLine().getStatusCode());

            if(response.getStatusLine().getStatusCode() != 201)
                return "Error";

            Log.d("Http Response:", response.toString());
        }

        catch (Exception e){

            e.printStackTrace();
            return "Error";

        }
        return "Success";
    }

    public String login(String emailString, String password){

        String email = null;
        try {
            email = URLEncoder.encode(emailString, "UTF-8");

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        String url = "https://" + email + ":" + password + "@" + localHost + ":8443/chaturaji-web-services/login";

        System.out.println(url);

        try{

            CredentialsProvider credsProvider = new BasicCredentialsProvider();

            HttpClientContext context = HttpClientContext.create();

            credsProvider.setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(email, password));

            context.setCredentialsProvider(credsProvider);

            HttpPost httpPost = new HttpPost(url);

            HttpResponse response = httpClient.execute(httpPost, context);

            System.out.println(response.getStatusLine().getStatusCode());

            if(response.getStatusLine().getStatusCode() != 202)
                return "Invalid";

            Log.d("Http Response:", response.toString());
        }

        catch (Exception e){

            e.printStackTrace();
            return "Error";

        }


        return "Success";
    }

    public void setEmailPassword(String email, String password){

        this.email = email;
        this.password = password;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

}
