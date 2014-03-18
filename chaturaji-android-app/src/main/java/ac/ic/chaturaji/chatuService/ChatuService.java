package ac.ic.chaturaji.chatuService;

import ac.ic.chaturaji.model.Colour;
import ac.ic.chaturaji.model.Move;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import ac.ic.chaturaji.uuid.UUIDFactory;
import ac.ic.chaturaji.websockets.GameMoveListener;
import ac.ic.chaturaji.websockets.WebSocketsClient;
import android.app.Activity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.*;
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
import org.apache.http.entity.StringEntity;
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
 * @author haider
 */
public class ChatuService {

    private static ChatuService instance;
    private final ObjectMapper objectMapper = new ObjectMapperFactory().createObjectMapper();
    private final UUIDFactory uuidFactory = new UUIDFactory();
    private String serverHost = "ec2-54-186-2-140.us-west-2.compute.amazonaws.com";
    private int serverPort = 8443;
    private DefaultHttpClient httpClient;
    private String email = "";
    private String password = "";
    private CookieStore cookieStoreLocal;
    private CredentialsProvider credsProviderLocal;
    private Player player;

    private ChatuService() {
    }

    public static synchronized ChatuService getInstance() {

        if (instance == null) {
            instance = new ChatuService();
            instance.setupClient();
        }

        return instance;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    private void setupClient() {

        // Acknowledgment to http://madurangasblogs.blogspot.co.uk/2013/08/avoiding-javaxnetsslsslpeerunverifiedex.html

        try {
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

        } catch (Exception e) {
            httpClient = new DefaultHttpClient();

        }
    }

    public void setupSocketClient(Activity activity) {

        try {

            WebSocketsClient webSocketsClient = new WebSocketsClient(serverHost);

            GameMoveListener gameMoveListener = new ClientGameMoveListener(activity);

            webSocketsClient.registerGameMoveListener(gameMoveListener, player.getId());

        } catch (Exception e) {

            System.out.println("Device not compatible");
        }

        System.out.println(player.getId());

    }


    public String getGames() {

        setupClient();

        String games = "Error";
        String url = "https://" + serverHost + ":" + serverPort + "/games";


        try {

            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            games = org.apache.http.util.EntityUtils.toString(entity);
            System.out.println(games);

        } catch (Exception e) {

            e.printStackTrace();

        }


        return games;
    }

    public String[] createGame(String AIOpps) {

        String[] reply = {"Error", " "};

        if (Integer.parseInt(AIOpps) > 3 || Integer.parseInt(AIOpps) < 0){

            reply[0] = "Invalid AI count";
            return reply;

        }

        setupClient();

        String url = "https://" + serverHost + ":" + serverPort + "/createGame";

        try {

            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStoreLocal);
            localContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProviderLocal);

            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("numberOfAIPlayers", AIOpps));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost, localContext);


            System.out.println(response.getStatusLine().getStatusCode());

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {

                return reply;
            }

            player = objectMapper.readValue(EntityUtils.toString(response.getEntity()), Player.class);

            reply[1] = player.getColour().toString();

        } catch (Exception e) {
            e.printStackTrace();
            return reply;
        }

        reply[0] = "Success";
        return reply;
    }

    public String[] joinGame(String gameId) {

        String[] reply = {"Good", "Success", "Colour"};
        setupClient();

        String url = "https://" + serverHost + ":" + serverPort + "/joinGame";

        try {

            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStoreLocal);
            localContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProviderLocal);

            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("gameId", gameId));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost, localContext);

            System.out.println(response.getStatusLine().getStatusCode());


            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {

                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);

                reply[0] = responseBody;
                reply[1] = "Bad request";

                return reply;

            } else if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {

                reply[0] = "Not 201 or 400";
                reply[1] = "Error";

                return reply;
            }

            player = objectMapper.readValue(EntityUtils.toString(response.getEntity()), Player.class);

            reply[2] = player.getColour().toString();

        } catch (Exception e) {

            e.printStackTrace();

            reply[0] = "Catch Exception";
            reply[1] = "Error";

            return reply;

        }

        return reply;
    }

    public String submitMove(int source, int destination) {

        setupClient();

        String url = "https://" + serverHost + ":" + serverPort + "/submitMove";

        try {

            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStoreLocal);
            localContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProviderLocal);

            HttpPost submitMove = new HttpPost(url);
            Move move = new Move(uuidFactory.generateUUID(), player.getGameId(), player.getColour(), source, destination);
            System.out.println("move = " + move);
            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(move));
            entity.setContentType("application/json;charset=UTF-8");
            submitMove.setEntity(entity);
            HttpResponse submitMoveResponse = httpClient.execute(submitMove, localContext);

            System.out.println(submitMoveResponse.getStatusLine().getStatusCode());

            if (submitMoveResponse.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
                return "Error";
            }

        } catch (Exception e) {

            e.printStackTrace();
            return "Error";

        }

        return "Success";
    }

    public String createAccount(String email, String password, String nickname) {

        setupClient();

        String url = "https://" + serverHost + ":" + serverPort + "/register";

        try {

            HttpPost httpPost = new HttpPost(url);

            httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                    new BasicNameValuePair("email", email),
                    new BasicNameValuePair("password", password),
                    new BasicNameValuePair("nickname", nickname)
            )));

            HttpResponse response = httpClient.execute(httpPost);

            System.out.println(response.getStatusLine().getStatusCode());

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
                return "Error";
            }

        } catch (Exception e) {

            e.printStackTrace();
            return "Error";

        }
        return "Success";
    }

    public String login(String emailString, String password) {

        setupClient();

        String email = null;

        try {
            email = URLEncoder.encode(emailString, "UTF-8");

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        String url = "https://" + serverHost + ":" + serverPort + "/login";

        System.out.println(url);

        try {

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

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
                return "Invalid";
            }

        } catch (Exception e) {

            e.printStackTrace();
            return "Error";

        }


        return "Success";
    }

    public void setEmailPassword(String email, String password) {

        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void clearCookieCred() {

        credsProviderLocal = null;
        cookieStoreLocal = null;

    }

    public Colour getPlayerColour() {

        return player.getColour();

    }

}
