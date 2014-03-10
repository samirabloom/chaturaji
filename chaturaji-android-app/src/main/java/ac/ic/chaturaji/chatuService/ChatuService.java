package ac.ic.chaturaji.chatuService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

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

    private static ChatuService instance;
    private String localHost = "192.168.1.110"; // Put your server address here...
    private DefaultHttpClient httpClient;
    private String email = "";
    private String password = "";
    private CookieStore cookieStoreLocal;
    private CredentialsProvider credsProviderLocal;

    private ChatuService(){}

    private void setupClient(){

        // Acknowledgment to http://madurangasblogs.blogspot.co.uk/2013/08/avoiding-javaxnetsslsslpeerunverifiedex.html

        try{
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
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

    public static synchronized  ChatuService getInstance(){

        if(instance==null){
            instance = new ChatuService();
            instance.setupClient();

        }

        return instance;
    }

    public String getGames(){

        setupClient();

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

        if(Integer.parseInt(AIOpps) > 3 || Integer.parseInt(AIOpps) < 0)
            return "Invalid AI count";

        setupClient();

        String url = "https://" + localHost + ":8443/chaturaji-web-services/game";

        try{

            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStoreLocal);
            localContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProviderLocal);

            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("numberOfAIPlayers", AIOpps));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost, localContext);


            System.out.println(response.getStatusLine().getStatusCode());

            if(response.getStatusLine().getStatusCode() != 201)
                return "Error";
        }

        catch (Exception e){

            e.printStackTrace();
            return "Error";

        }

        return "Success";
    }

    public String[] joinGame(String gameId){

        String[] reply = {"Good", "Success"};
        setupClient();

        String url = "https://" + localHost + ":8443/chaturaji-web-services/joinGame";

        try{

            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStoreLocal);
            localContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProviderLocal);

            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("gameId", gameId));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost, localContext);

            System.out.println(response.getStatusLine().getStatusCode());


            if(response.getStatusLine().getStatusCode() == 400){

                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);

                reply[0] = responseBody;
                reply[1] = "Bad request";

                return reply;

            }

            else if(response.getStatusLine().getStatusCode() != 201){

                reply[0] = "Not 201 or 400";
                reply[1] = "Error";

                return reply;
            }
        }

        catch (Exception e){

            e.printStackTrace();

            reply[0] = "Catch Exception";
            reply[1] = "Error";

            return reply;

        }

        return reply;
    }

    public String createAccount(String email, String password, String nickname){

        setupClient();

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

        }

        catch (Exception e){

            e.printStackTrace();
            return "Error";

        }
        return "Success";
    }

    public String login(String emailString, String password){

        setupClient();

        String email = null;

        try {
            email = URLEncoder.encode(emailString, "UTF-8");

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        String url = "https://" + localHost + ":8443/chaturaji-web-services/login";

        System.out.println(url);

        try{

            HttpContext localContext = new BasicHttpContext();

            CredentialsProvider credsProvider = new BasicCredentialsProvider();

            CookieStore cookieStore = new BasicCookieStore();

            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            credsProvider.setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(email, password));

            localContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProvider);

            HttpPost httpPost = new HttpPost(url);

            HttpResponse response = httpClient.execute(httpPost, localContext);

            credsProviderLocal = (CredentialsProvider) localContext.getAttribute(ClientContext.CREDS_PROVIDER);
            cookieStoreLocal = (CookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);

            System.out.println(response.getStatusLine().getStatusCode());

            if(response.getStatusLine().getStatusCode() != 202)
                return "Invalid";

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

    public void clearCookieCred(){

        credsProviderLocal = null;
        cookieStoreLocal = null;

    }

}
