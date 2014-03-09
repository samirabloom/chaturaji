package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;

/**
 * @author samirarabbanian
 */
public class GameControllerFullIntegrationTest {

    private static Tomcat tomcat;
    private static KeyStore trustStore;

    @BeforeClass
    public static void setupFixture() throws Exception {
        String classLocation = GameControllerFullIntegrationTest.class.getCanonicalName().replace(".", "/") + ".class";
        String projectBase = GameControllerFullIntegrationTest.class.getClassLoader().getResource(classLocation).toString().replace(classLocation, "../../").replace("file:", "");

        // start proxy (in tomcat)
        tomcat = new Tomcat();
        tomcat.setBaseDir(new File(".").getCanonicalPath() + File.separatorChar + "tomcat");

        // add http port
        tomcat.setPort(8080);

        // add https port
        Connector httpsConnector = new Connector();
        httpsConnector.setPort(8443);
        httpsConnector.setSecure(true);
        httpsConnector.setScheme("https");
        httpsConnector.setAttribute("keystorePass", "changeit");
        httpsConnector.setAttribute("keystoreFile", projectBase + "../keystore");
        httpsConnector.setAttribute("clientAuth", "false");
        httpsConnector.setAttribute("sslProtocol", "TLS");
        httpsConnector.setAttribute("SSLEnabled", true);
        Service service = tomcat.getService();
        service.addConnector(httpsConnector);

        // add servlet
        Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());
        ContextConfig contextConfig = new ContextConfig();
        ctx.addLifecycleListener(contextConfig);
        contextConfig.setDefaultWebXml(projectBase + "src/main/webapp/WEB-INF/web.xml");

        // control logging level
        java.util.logging.Logger.getLogger("").setLevel(Level.FINER);

        // start server
        tomcat.start();

        // load key store for certificates
        trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fileInputStream = new FileInputStream(new File(projectBase + "../keystore"))) {
            trustStore.load(fileInputStream, "changeit".toCharArray());
        }
    }

    @AfterClass
    public static void shutdownFixture() throws LifecycleException {
        // stop server
        tomcat.stop();
    }

    @Test
    public void shouldRegisterUser() throws Exception {
        // given
        HttpClient httpClient = createApacheClient();

        // when
        HttpPost register = new HttpPost("https://127.0.0.1:8443/register");
        register.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("email", "user_three@example.com"),
                new BasicNameValuePair("password", "password_three"),
                new BasicNameValuePair("nickname", "my_nickname")
        )));
        HttpResponse createGameResponse = httpClient.execute(register);

        // then
        assertEquals(HttpStatus.CREATED.value(), createGameResponse.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldRetrieveListOfCurrentGamesAndCreateGame() throws Exception {
        // given
        HttpClient httpClient = createApacheClient();

        // --- get list of games ---

        // when
        HttpGet getGames = new HttpGet("https://127.0.0.1:8443/games");
        HttpResponse getGamesResponse = httpClient.execute(getGames);
        HttpEntity entity = getGamesResponse.getEntity();
        String content = EntityUtils.toString(entity);
        Game[] games = new ObjectMapperFactory().createObjectMapper().readValue(content, Game[].class);

        // then
        assertEquals(4, games.length);

        // --- login ---

        // given
        registerUser("user_two@example.com", "password_two");

        // when
        HttpPost login = new HttpPost("https://user_two%40example.com:password_two@127.0.0.1:8443/login");
        HttpResponse loginResponse = httpClient.execute(login);

        // then
        assertEquals(HttpStatus.ACCEPTED.value(), loginResponse.getStatusLine().getStatusCode());

        // --- create game ---

        // when
        HttpPost createGame = new HttpPost("https://127.0.0.1:8443/game");
        createGame.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("numberOfAIPlayers", "3")
        )));
        HttpResponse createGameResponse = httpClient.execute(createGame);

        // then
        assertEquals(HttpStatus.CREATED.value(), createGameResponse.getStatusLine().getStatusCode());
        assertEquals(games.length + 1, numberOfGames());
    }

    public void registerUser(String email, String password) throws Exception {
        HttpClient httpClient = createApacheClient();
        HttpPost register = new HttpPost("https://127.0.0.1:8443/register");
        register.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("email", email),
                new BasicNameValuePair("nickname", "some_nickname"),
                new BasicNameValuePair("password", password)
        )));
        httpClient.execute(register);
    }

    @Test
    public void shouldNotAllowGameCreationIfNotLoggedIn() throws Exception {
        // given
        HttpClient httpClient = createApacheClient();

        // when
        HttpPost createGame = new HttpPost("https://127.0.0.1:8443/game");
        createGame.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("numberOfAIPlayers", "3")
        )));
        HttpResponse createGameResponse = httpClient.execute(createGame);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), createGameResponse.getStatusLine().getStatusCode());
    }

    private CloseableHttpClient createApacheClient() throws Exception {
        return HttpClients.custom()
                // make sure the tests don't block when server fails to start up
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout((int) TimeUnit.SECONDS.toMillis(4))
                        .build())
                .setSslcontext(
                        SSLContexts
                                .custom()
                                .loadTrustMaterial(trustStore, new TrustStrategy() {
                                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                        return true;
                                    }
                                })
                                .build()
                )
                .setHostnameVerifier(new AllowAllHostnameVerifier())
                .build();
    }

    public int numberOfGames() throws Exception {
        HttpClient httpClient = createApacheClient();
        HttpGet request = new HttpGet("https://127.0.0.1:8443/games");
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return new ObjectMapperFactory().createObjectMapper().readValue(EntityUtils.toString(entity), Game[].class).length;
    }
}
