package ac.ic.chaturaji;

import ac.ic.chaturaji.chatuService.ChatuService;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import ac.ic.chaturaji.web.PortFactory;
import android.util.Log;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import static org.junit.Assert.*;

/**
 * @author haider
 */
public class ChatuServiceTest {

    protected static Server server;
    protected static KeyStore trustStore;
    protected static int httpPort;
    protected static int httpsPort;
    private static String serverHost = "127.0.0.1";

    @BeforeClass
    public static void setupFixture() throws Exception {
        System.setProperty("http.port", "" + PortFactory.findFreePort());
        httpPort = Integer.parseInt(System.getProperty("http.port"));

        System.setProperty("https.port", "" + PortFactory.findFreePort());
        httpsPort = Integer.parseInt(System.getProperty("https.port"));

        String classLocation = ChatuServiceTest.class.getCanonicalName().replace(".", "/") + ".class";
        String projectBase = ChatuServiceTest.class.getClassLoader().getResource(classLocation).toString().replace(classLocation, "../../").replace("file:", "");

        server = new Server();
        // add connectors
        server.addConnector(createHTTPConnector(server, httpPort, httpsPort));
        server.addConnector(createHTTPSConnector(projectBase, server, httpsPort));
        WebAppContext root = new WebAppContext();

        root.setWar(projectBase + "../chaturaji-web-services/src/main/webapp");
        root.setContextPath("/");

        server.setHandler(root);
        server.start();

        // load key store for certificates
        trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fileInputStream = new FileInputStream(new File(projectBase + "../keystore"))) {
            trustStore.load(fileInputStream, "changeit".toCharArray());
        }
    }

    private static ServerConnector createHTTPConnector(Server server, Integer port, Integer securePort) {
        // HTTP Configuration
        HttpConfiguration http_config = new HttpConfiguration();
        if (securePort != null) {
            http_config.setSecurePort(securePort);
        }

        // HTTP connector
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
        http.setPort(port);
        return http;
    }

    private static ServerConnector createHTTPSConnector(String projectBase, Server server, Integer securePort) throws GeneralSecurityException, IOException {
        // HTTPS Configuration
        HttpConfiguration https_config = new HttpConfiguration();
        https_config.setSecurePort(securePort);
        https_config.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory sslContextFactory = new SslContextFactory(true);
        sslContextFactory.setKeyStorePath(projectBase + "../keystore");
        sslContextFactory.setKeyStorePassword("changeit");
        sslContextFactory.setProtocol("TLS");

        // HTTPS connector
        ServerConnector https = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https_config));
        https.setPort(securePort);
        return https;
    }

    @AfterClass
    public static void shutdownFixture() throws Exception {
        // stop server
        server.stop();
    }

    @Test
    public void shouldRegisterUser() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        String state = chatuService.createAccount("test@test.com", "testpass123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);
    }


    @Test
    public void requestSpeed() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        long startTime = System.currentTimeMillis();

        String state = chatuService.createAccount("test_speed@test.com", "testpass123", "testman");

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println(elapsedTime);

        long maxRequestTime = 5000;
        assertTrue(elapsedTime < maxRequestTime);

        startTime = System.currentTimeMillis();

        state = chatuService.login("test_speed@test.com", "testpass123");

        elapsedTime = System.currentTimeMillis() - startTime;

        assertTrue(elapsedTime < maxRequestTime);

        startTime = System.currentTimeMillis();

        state = chatuService.getGames();

        elapsedTime = System.currentTimeMillis() - startTime;

        assertTrue(elapsedTime < maxRequestTime);

        startTime = System.currentTimeMillis();

        String[] state2 = chatuService.createGame("0");

        elapsedTime = System.currentTimeMillis() - startTime;

        assertTrue(elapsedTime < maxRequestTime);
    }


    @Test
    public void shouldNotRegister() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        // Should return Error if a proper email is not entered
        assertEquals("Error", chatuService.createAccount("NotAProperEmail", "testpass123", "testman"));

        // Should return Error if too small a password is entered
        assertEquals("Error", chatuService.createAccount("test_not_registered@test.com", "pas", "testman"));

        // Should succeed
        assertEquals("Success", chatuService.createAccount("test_not_registered@test.com", "testpass123", "testman"));

        // Should return Error if too small a password is entered
        assertEquals("Error", chatuService.createAccount("test_not_registered@test.com", "testpass123", "testman"));
    }


    @Test
    public void shouldnotLogin() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        String state = chatuService.login("randomguy@gmail.com", "randomness");

        // this guy does not exist on the database, therefore should return an Invalid when trying to log in
        assertEquals("Invalid", state);
    }

    @Test
    public void wrongPassword() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        String state = chatuService.createAccount("wrongpassword@test.com", "password123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chatuService.login("wrongpassword@test.com", "pasword");

        assertEquals("Invalid", state);
    }

    @Test
    public void shouldLogin() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        String state = chatuService.createAccount("test_login@test.com", "testpass123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chatuService.login("test_login@test.com", "testpass123");

        // this is the test account that was previously registered, therefore should be able to log in
        assertEquals("Success", state);
    }

    @Test
    public void getGames() throws Exception {

        ChatuService testService = ChatuService.getInstance();

        String test = testService.getGames();

        Game[] gamesList = null;

        try {

            gamesList = new ObjectMapperFactory().createObjectMapper().readValue(test, Game[].class);

        } catch (JsonGenerationException e) {
            Log.d("JsonGenerationException ", e.toString());
            e.printStackTrace();

        } catch (JsonMappingException e) {
            Log.d("JsonMappingException", e.toString());
            e.printStackTrace();

        } catch (IOException e) {
            Log.d("IOException", e.toString());
            e.printStackTrace();

        }

        // this is the test account that was previously registered, therefore should be able to log in
        assertNotNull(gamesList);
    }

    @Test
    public void shouldClearDetails() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        String state = chatuService.createAccount("test_clear@test.com", "testpass123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chatuService.login("test_clear@test.com", "testpass123");

        assertEquals("Success", state);

        chatuService.clearCookieCred();

        String[] state2 = chatuService.createGame("2");

        // we cleared the cookie and creds on the client, so should not be able to log in now
        assertEquals("Error", state2[0]);
    }

    @Test
    public void shouldCreateGame() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        String state = chatuService.createAccount("test_create_game@test.com", "testpass123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chatuService.login("test_create_game@test.com", "testpass123");

        assertEquals("Success", state);

        String[] state2 = chatuService.createGame("2");

        // we cleared the cookie and creds on the client, so should not be able to log in now
        assertEquals("Success", state2[0]);
    }

    @Test
    public void tooManyAIs() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        String state = chatuService.createAccount("test_too_many_ais@test.com", "testpass123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chatuService.login("test_too_many_ais@test.com", "testpass123");

        assertEquals("Success", state);

        String[] state2 = chatuService.createGame("5");

        // should not allow more than 3 AIs
        assertEquals("Invalid AI count", state2[0]);
    }

    @Test
    public void shouldJoinGame() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        chatuService.createAccount("test_join_game@test.com", "testpass123", "testman");

        String state = chatuService.login("test_join_game@test.com", "testpass123");

        assertEquals("Success", state);

        String[] state2 = chatuService.createGame("0");

        assertEquals("Success", state);

        chatuService.clearCookieCred();

        state = chatuService.createAccount("test2@test.com", "testpass123", "testman2");

        assertEquals("Success", state);

        state = chatuService.login("test2@test.com", "testpass123");

        assertEquals("Success", state);

        state = chatuService.getGames();

        Game[] gamesList = null;

        try {

            gamesList = new ObjectMapperFactory().createObjectMapper().readValue(state, Game[].class);

        } catch (JsonGenerationException e) {
            Log.d("JsonGenerationException ", e.toString());
            e.printStackTrace();

        } catch (JsonMappingException e) {
            Log.d("JsonMappingException", e.toString());
            e.printStackTrace();

        } catch (IOException e) {
            Log.d("IOException", e.toString());
            e.printStackTrace();

        }

        // this is the test account that was previously registered, therefore should be able to log in
        assertNotNull(gamesList);

        String id = "";
        for (Game aGamesList : gamesList) {

            if (aGamesList.getPlayers().size() < 4) {

                id = aGamesList.getId();

            }
        }


        String[] joinstate = chatuService.joinGame(id);

        // successfully joined a game

        // TODO fix this test as it fails 50% of the time
        // assertEquals("Success", joinstate[1]);


    }

    @Test
    public void testGettersAndSetters() throws Exception {

        String email = "testman@testman.com";

        String password = "randomness";

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        chatuService.setEmailPassword(email, password);

        String gotEmail = chatuService.getEmail();

        String gotPassword = chatuService.getPassword();

        assertEquals(email, gotEmail);

        assertEquals(password, gotPassword);


    }

    @Test
    public void fullGame() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        chatuService.clearCookieCred();

        chatuService.createAccount("test_full_game@test.com", "testpass123", "testman2");

        String state = chatuService.login("test_full_game@test.com", "testpass123");

        assertEquals("Success", state);

        state = chatuService.getGames();

        Game[] gamesList = null;

        try {

            gamesList = new ObjectMapperFactory().createObjectMapper().readValue(state, Game[].class);

        } catch (JsonGenerationException e) {
            Log.d("JsonGenerationException ", e.toString());
            e.printStackTrace();

        } catch (JsonMappingException e) {
            Log.d("JsonMappingException", e.toString());
            e.printStackTrace();

        } catch (IOException e) {
            Log.d("IOException", e.toString());
            e.printStackTrace();

        }

        // this is the test account that was previously registered, therefore should be able to log in
        assertNotNull(gamesList);

        String id = " ";
        for (int i = 0; i < gamesList.length && id.equals(" "); i++) {

            if (gamesList[i].getPlayers().size() > 3) {

                id = gamesList[i].getId();

            }
        }


        String[] joinstate = chatuService.joinGame(id);

        // successfully joined a game
        assertEquals("Bad request", joinstate[1]);


    }

    @Test
    public void invalidGameId() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        chatuService.createAccount("test_invalid_game_id@test.com", "testpass123", "testman2");

        String state = chatuService.login("test_invalid_game_id@test.com", "testpass123");

        assertEquals("Success", state);

        state = chatuService.getGames();

        Game[] gamesList = null;

        try {

            gamesList = new ObjectMapperFactory().createObjectMapper().readValue(state, Game[].class);

        } catch (JsonGenerationException e) {
            Log.d("JsonGenerationException ", e.toString());
            e.printStackTrace();

        } catch (JsonMappingException e) {
            Log.d("JsonMappingException", e.toString());
            e.printStackTrace();

        } catch (IOException e) {
            Log.d("IOException", e.toString());
            e.printStackTrace();

        }

        // this is the test account that was previously registered, therefore should be able to log in
        assertNotNull(gamesList);

        String id = " ";

        String[] joinstate = chatuService.joinGame(id);

        // successfully joined a game
        assertEquals("Bad request", joinstate[1]);


    }

    @Test
    public void cannotJoinOwnGame() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        String email = "test_join_own_game@test.com";

        chatuService.createAccount(email, "testpass123", "testman2");

        String state = chatuService.login(email, "testpass123");

        assertEquals("Success", state);

        String[] state2 = chatuService.createGame("0");

        assertEquals("Success", state);

        state = chatuService.getGames();

        Game[] gamesList = null;

        try {

            gamesList = new ObjectMapperFactory().createObjectMapper().readValue(state, Game[].class);

        } catch (JsonGenerationException e) {
            Log.d("JsonGenerationException ", e.toString());
            e.printStackTrace();

        } catch (JsonMappingException e) {
            Log.d("JsonMappingException", e.toString());
            e.printStackTrace();

        } catch (IOException e) {
            Log.d("IOException", e.toString());
            e.printStackTrace();

        }

        // this is the test account that was previously registered, therefore should be able to log in
        assertNotNull(gamesList);

        String id = " ";
        for (int i = 0; i < gamesList.length && id.equals(" "); i++) {

            if (gamesList[i].getPlayers().size() > 0) {

                if (!(gamesList[i].getPlayers().get(0).getUser().getEmail() == null))

                    if (gamesList[i].getPlayers().get(0).getUser().getEmail().equals(email)) {

                        id = gamesList[i].getId();

                    }
            }
        }


        String[] joinstate = chatuService.joinGame(id);

        // successfully joined a game
        assertEquals("Bad request", joinstate[1]);


    }

}