package ac.ic.chaturaji.chatuService;

import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Move;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.Result;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import ac.ic.chaturaji.uuid.UUIDFactory;
import ac.ic.chaturaji.websockets.GameMoveListener;
import ac.ic.chaturaji.websockets.WebSocketsClient;
import android.app.Activity;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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

import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author haider
 */
public class ChaturajiService {

    private static final String TAG = "ChatuService";
    private static ChaturajiService instance;
    private final ObjectMapper objectMapper = new ObjectMapperFactory().createObjectMapper();
    private final UUIDFactory uuidFactory = new UUIDFactory();
    //    private String serverHostAndPort = "ec2-54-186-2-140.us-west-2.compute.amazonaws.com:8443";
    //    private String serverHostAndPort = "192.168.1.100:8443";
    private String serverHostAndPort = "msc14-prj-14.doc.ic.ac.uk:55443";
    private DefaultHttpClient httpClient;
    private String email = "";
    private String password = "";
    private CookieStore cookieStoreLocal;
    private CredentialsProvider credsProviderLocal;
    private Player player;
    private String currentGameID = null;

    private ChaturajiService() {
    }

    public static synchronized ChaturajiService getInstance() {

        if (instance == null) {
            instance = new ChaturajiService();
            instance.setupClient();
        }

        return instance;
    }

    public void setServerHostAndPort(String serverHost) {
        this.serverHostAndPort = serverHost;
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
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 55080));
            registry.register(new Scheme("https", sf, 55443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            httpClient = new DefaultHttpClient(ccm, params);

        } catch (Exception e) {
            httpClient = new DefaultHttpClient();

        }
    }

    public void setupSocketClient(final Activity activity) {
        try {
            WebSocketsClient webSocketsClient = new WebSocketsClient(StringUtils.substringBefore(serverHostAndPort, ":"));
            webSocketsClient.registerGameMoveListener(new GameMoveListener() {
                @Override
                public void onMoveCompleted(Result result) {
                    ((OnMoveCompleteListener) activity).updateGame(result);
                }
            }, player.getId());
        } catch (Exception e) {
            Log.d(TAG, "Exception while receiving move via web-sockets", e);
        }
    }


    public Game[] getGames() {
        setupClient();

        try {
            HttpResponse response = httpClient.execute(new HttpGet("https://" + serverHostAndPort + "/games"));
            String games = EntityUtils.toString(response.getEntity());
            return objectMapper.readValue(games, Game[].class);
        } catch (Exception e) {
            Log.d(TAG, "Exception while loading games", e);
        }

        return new Game[]{};
    }

    public Game[] getGamesHistory() {
        setupClient();

        try {
            HttpResponse response = httpClient.execute(new HttpGet("https://" + serverHostAndPort + "/gameHistory"));
            String games = EntityUtils.toString(response.getEntity());
            return objectMapper.readValue(games, Game[].class);
        } catch (Exception e) {
            Log.d(TAG, "Exception while loading games", e);
        }

        return new Game[]{};
    }

    public String[] createGame(String numberOfComputerOpponents, String difficulty) {

        String[] reply;

        if (Integer.parseInt(numberOfComputerOpponents) > 3 || Integer.parseInt(numberOfComputerOpponents) < 0) {
            reply = new String[]{
                    "Invalid Number Of Computer Opponents",
                    ""
            };
        } else {
            setupClient();
            try {
                HttpContext localContext = new BasicHttpContext();
                localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStoreLocal);
                localContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProviderLocal);

                HttpPost httpPost = new HttpPost("https://" + serverHostAndPort + "/createGame");
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("numberOfAIPlayers", numberOfComputerOpponents));
                nameValuePairs.add(new BasicNameValuePair("aiLevel", difficulty));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost, localContext);
                switch (response.getStatusLine().getStatusCode()) {
                    case HttpStatus.SC_UNAUTHORIZED:
                        reply = new String[]{
                                "Unauthorized",
                                ""
                        };
                        break;
                    case HttpStatus.SC_BAD_REQUEST:
                        reply = new String[]{
                                "Bad request",
                                ""
                        };
                        break;
                    case HttpStatus.SC_CREATED:
                        player = objectMapper.readValue(EntityUtils.toString(response.getEntity()), Player.class);
                        reply = new String[]{
                                "Success",
                                String.valueOf(player.getColour())
                        };
                        break;
                    default:
                        reply = new String[]{
                                "Error",
                                ""
                        };
                        break;
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception while creating game", e);
                reply = new String[]{
                        "Error",
                        ""
                };
            }
        }
        return reply;
    }

    public String[] joinGame(String gameId) {

        setCurrentGameID(gameId);

        setupClient();

        String[] reply;
        try {

            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStoreLocal);
            localContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProviderLocal);

            HttpPost httpPost = new HttpPost("https://" + serverHostAndPort + "/joinGame");
            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("gameId", gameId));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost, localContext);

            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_UNAUTHORIZED:
                    reply = new String[]{
                            response.getStatusLine().getReasonPhrase(),
                            "Unauthorized",
                            ""
                    };
                    break;
                case HttpStatus.SC_BAD_REQUEST:
                    reply = new String[]{
                            EntityUtils.toString(response.getEntity()),
                            "Bad request",
                            ""
                    };
                    break;
                case HttpStatus.SC_CREATED:
                    player = objectMapper.readValue(EntityUtils.toString(response.getEntity()), Player.class);
                    reply = new String[]{
                            "Good",
                            "Success",
                            String.valueOf(player.getColour())
                    };
                    break;
                default:
                    reply = new String[]{
                            EntityUtils.toString(response.getEntity()),
                            "Error",
                            ""
                    };
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception while joining game", e);
            reply = new String[]{
                    e.getMessage(),
                    "Error",
                    ""
            };
        }

        return reply;
    }

    public String[] replayGame (String gameId) {

        setupClient();

        String[] reply;
        try {

            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStoreLocal);
            localContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProviderLocal);

            HttpPost httpPost = new HttpPost("https://" + serverHostAndPort + "/replayGame");
            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("gameId", gameId));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost, localContext);

            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_UNAUTHORIZED:
                    reply = new String[]{
                            response.getStatusLine().getReasonPhrase(),
                            "Unauthorized",
                            ""
                    };
                    break;
                case HttpStatus.SC_BAD_REQUEST:
                    reply = new String[]{
                            EntityUtils.toString(response.getEntity()),
                            "Bad request",
                            ""
                    };
                    break;
                case HttpStatus.SC_CREATED:
                    player = objectMapper.readValue(EntityUtils.toString(response.getEntity()), Player.class);
                    reply = new String[]{
                            "Good",
                            "Success",
                            String.valueOf(player.getColour())
                    };
                    break;
                default:
                    reply = new String[]{
                            EntityUtils.toString(response.getEntity()),
                            "Error",
                            ""
                    };
                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception while joining game", e);
            reply = new String[]{
                    e.getMessage(),
                    "Error",
                    ""
            };
        }

        return reply;
    }

    public String submitMove(int source, int destination) {

        setupClient();

        try {

            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStoreLocal);
            localContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProviderLocal);

            HttpPost submitMove = new HttpPost("https://" + serverHostAndPort + "/submitMove");

            Move move = new Move(uuidFactory.generateUUID(), player.getGameId(), player.getColour(), source, destination);
            StringEntity entity = new StringEntity(objectMapper.writeValueAsString(move));
            entity.setContentType("application/json;charset=UTF-8");
            submitMove.setEntity(entity);

            HttpResponse response = httpClient.execute(submitMove, localContext);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
                return "Success";
            } else {
                return EntityUtils.toString(response.getEntity());
            }

        } catch (Exception e) {
            Log.d(TAG, "Exception while submitting move", e);
            return "Error";
        }
    }

    public String register(String email, String password, String nickname) {

        setupClient();

        try {

            HttpPost httpPost = new HttpPost("https://" + serverHostAndPort + "/register");
            httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                    new BasicNameValuePair("email", email),
                    new BasicNameValuePair("password", password),
                    new BasicNameValuePair("nickname", nickname)
            )));

            HttpResponse response = httpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                return "Success";
            } else {
                return EntityUtils.toString(response.getEntity());
            }

        } catch (Exception e) {
            Log.d(TAG, "Exception while registering", e);
            return "Error";
        }
    }

    public String login(String emailString, String password) {

        setupClient();

        try {

            HttpContext localContext = new BasicHttpContext();

            CookieStore cookieStore = new BasicCookieStore();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(URLEncoder.encode(emailString, "UTF-8"), password));
            localContext.setAttribute(ClientContext.CREDS_PROVIDER, credentialsProvider);

            HttpResponse response = httpClient.execute(new HttpPost("https://" + serverHostAndPort + "/login"), localContext);

            credsProviderLocal = (CredentialsProvider) localContext.getAttribute(ClientContext.CREDS_PROVIDER);
            cookieStoreLocal = (CookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
                return "Success";
            } else {
                return "There was a problem logging in. Have you entered the correct details?";
            }

        } catch (Exception e) {
            Log.d(TAG, "Exception while logging in", e);
            return "Error";
        }
    }

    public String logout() {

        setupClient();

        try {

            HttpContext localContext = new BasicHttpContext();
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStoreLocal);
            localContext.setAttribute(ClientContext.CREDS_PROVIDER, credsProviderLocal);

            httpClient.execute(new HttpPost("https://" + serverHostAndPort + "/logout"), localContext);

            return "Success";

        } catch (Exception e) {
            Log.d(TAG, "Exception while logging out", e);
            return "Error";
        }
    }

    public String updatePassword(String emailString) {

        setupClient();

        try {
            HttpPost httpPost = new HttpPost("https://" + serverHostAndPort + "/sendUpdatePasswordEmail");
            httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                    new BasicNameValuePair("email", emailString)
            )));

            HttpResponse response = httpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
                return "Success";
            } else {
                return EntityUtils.toString(response.getEntity());
            }

        } catch (Exception e) {
            Log.d(TAG, "Exception while request update password email", e);
            return "Error";
        }
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

    public void setCurrentGameID(String gameID){

        currentGameID = gameID;

    }

    public String getCurrentGameID(){

        return currentGameID;

    }

}
