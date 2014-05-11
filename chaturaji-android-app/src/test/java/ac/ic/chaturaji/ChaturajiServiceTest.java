package ac.ic.chaturaji;

import ac.ic.chaturaji.chatuService.ChaturajiService;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.web.PortFactory;
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
public class ChaturajiServiceTest {

    protected static Server server;
    protected static KeyStore trustStore;
    protected static int httpPort;
    protected static int httpsPort;
    private static String serverHostAndPort = "127.0.0.1";

    @BeforeClass
    public static void setupFixture() throws Exception {
        System.setProperty("http.port", "" + PortFactory.findFreePort());
        httpPort = Integer.parseInt(System.getProperty("http.port"));

        System.setProperty("https.port", "" + PortFactory.findFreePort());
        httpsPort = Integer.parseInt(System.getProperty("https.port"));

        String classLocation = ChaturajiServiceTest.class.getCanonicalName().replace(".", "/") + ".class";
        String projectBase = ChaturajiServiceTest.class.getClassLoader().getResource(classLocation).toString().replace(classLocation, "../../").replace("file:", "");

        server = new Server();
        // add connectors
        server.addConnector(createHTTPConnector(server, httpPort, httpsPort));
        server.addConnector(createHTTPSConnector(projectBase, server, httpsPort));
        serverHostAndPort += ":" + httpsPort;
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

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        String state = chaturajiService.register("test@test.com", "testpass123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);
    }


    @Test
    public void requestSpeed() throws Exception {

        long maxRequestTime = 5000;

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        // register
        long startTime = System.currentTimeMillis();
        chaturajiService.register("test_speed@test.com", "testpass123", "testman");
        long elapsedTime = System.currentTimeMillis() - startTime;
        assertTrue(elapsedTime < maxRequestTime);

        // login
        startTime = System.currentTimeMillis();
        chaturajiService.login("test_speed@test.com", "testpass123");
        elapsedTime = System.currentTimeMillis() - startTime;
        assertTrue(elapsedTime < maxRequestTime);

        // get games
        startTime = System.currentTimeMillis();
        chaturajiService.getGames();
        elapsedTime = System.currentTimeMillis() - startTime;
        assertTrue(elapsedTime < maxRequestTime);

        // create game
        startTime = System.currentTimeMillis();
        chaturajiService.createGame("0");
        elapsedTime = System.currentTimeMillis() - startTime;
        assertTrue(elapsedTime < maxRequestTime);
    }


    @Test
    public void shouldNotRegister() throws Exception {

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        // Should return Error if a proper email is not entered
        assertEquals("email - Please provide a valid email", chaturajiService.register("NotAProperEmail", "testpass123", "testman"));

        // Should return Error if too small a password is entered
        assertEquals("password - Please provide a password of 8 or more characters with at least 1 digit and 1 letter", chaturajiService.register("test_not_registered@test.com", "pas", "testman"));

        // Should succeed
        assertEquals("Success", chaturajiService.register("test_not_registered@test.com", "testpass123", "testman"));

        // Should return Error if too small a password is entered
        assertEquals("A user already exists with that email address", chaturajiService.register("test_not_registered@test.com", "testpass123", "testman"));
    }


    @Test
    public void shouldnotLogin() throws Exception {

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        String state = chaturajiService.login("randomguy@gmail.com", "randomness");

        // this guy does not exist on the database, therefore should return an Invalid when trying to log in
        assertEquals("There was a problem logging in. Have you entered the correct details?", state);
    }

    @Test
    public void wrongPassword() throws Exception {

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        String state = chaturajiService.register("wrongpassword@test.com", "password123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chaturajiService.login("wrongpassword@test.com", "pasword");

        assertEquals("There was a problem logging in. Have you entered the correct details?", state);
    }

    @Test
    public void updatePassword() throws Exception {

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        String state = chaturajiService.register("wrongpassword123@test.com", "password123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chaturajiService.updatePassword("wrongpassword123@test.com");

        assertEquals("Success", state);
    }


    @Test
    public void shouldLogin() throws Exception {

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        String state = chaturajiService.register("test_login@test.com", "testpass123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chaturajiService.login("test_login@test.com", "testpass123");

        // this is the test account that was previously registered, therefore should be able to log in
        assertEquals("Success", state);
    }

    @Test
    public void shouldLogout() throws Exception {

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        String state = chaturajiService.register("test_loginx@test.com", "testpass123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chaturajiService.login("test_loginx@test.com", "testpass123");

        // this is the test account that was previously registered, therefore should be able to log in
        assertEquals("Success", state);

        state = chaturajiService.logout();

        // this is the test account that was previously registered, therefore should be able to log in
        assertEquals("Success", state);
    }

    @Test
    public void getGames() throws Exception {

        ChaturajiService testService = ChaturajiService.getInstance();

        Game[] gamesList = testService.getGames();

        // this is the test account that was previously registered, therefore should be able to log in
        assertNotNull(gamesList);
    }

    @Test
    public void shouldClearDetails() throws Exception {

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        String state = chaturajiService.register("test_clear@test.com", "testpass123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chaturajiService.login("test_clear@test.com", "testpass123");

        assertEquals("Success", state);

        chaturajiService.clearCookieCred();

        String[] state2 = chaturajiService.createGame("2");

        // we cleared the cookie and creds on the client, so should not be able to log in now
        assertEquals("Unauthorized", state2[0]);
    }

    @Test
    public void shouldCreateGame() throws Exception {

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        String state = chaturajiService.register("test_create_game@test.com", "testpass123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chaturajiService.login("test_create_game@test.com", "testpass123");

        assertEquals("Success", state);

        String[] state2 = chaturajiService.createGame("2");

        // we cleared the cookie and creds on the client, so should not be able to log in now
        assertEquals("Success", state2[0]);
    }

    @Test
    public void tooManyAIs() throws Exception {

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        String state = chaturajiService.register("test_too_many_ais@test.com", "testpass123", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chaturajiService.login("test_too_many_ais@test.com", "testpass123");

        assertEquals("Success", state);

        String[] state2 = chaturajiService.createGame("5");

        // should not allow more than 3 AIs
        assertEquals("Invalid Number Of Computer Opponents", state2[0]);
    }

    @Test
    public void shouldJoinGame() throws Exception {

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        chaturajiService.register("test_join_game@test.com", "testpass123", "testman");

        String state = chaturajiService.login("test_join_game@test.com", "testpass123");

        assertEquals("Success", state);

        String[] state2 = chaturajiService.createGame("0");

        assertEquals("Success", state2[0]);

        chaturajiService.clearCookieCred();

        state = chaturajiService.register("test2@test.com", "testpass123", "testman2");

        assertEquals("Success", state);

        state = chaturajiService.login("test2@test.com", "testpass123");

        assertEquals("Success", state);

        Game[] gamesList = chaturajiService.getGames();

        // this is the test account that was previously registered, therefore should be able to log in
        assertNotNull(gamesList);

        String id = "";
        for (Game aGamesList : gamesList) {

            if (aGamesList.getPlayers().size() < 4) {

                id = aGamesList.getId();

            }
        }

        String[] joinstate = chaturajiService.joinGame(id);

        // successfully joined a game
        assertEquals("Success", joinstate[1]);
    }

    @Test
    public void testGettersAndSetters() throws Exception {

        String email = "testman@testman.com";

        String password = "randomness";

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        chaturajiService.setEmailPassword(email, password);

        String gotEmail = chaturajiService.getEmail();

        String gotPassword = chaturajiService.getPassword();

        assertEquals(email, gotEmail);

        assertEquals(password, gotPassword);


    }

    @Test
    public void fullGame() throws Exception {

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        chaturajiService.clearCookieCred();

        chaturajiService.register("test_full_game@test.com", "testpass123", "testman2");

        String state = chaturajiService.login("test_full_game@test.com", "testpass123");

        assertEquals("Success", state);

        Game[] gamesList = chaturajiService.getGames();

        // this is the test account that was previously registered, therefore should be able to log in
        assertNotNull(gamesList);

        String id = " ";
        for (int i = 0; i < gamesList.length && id.equals(" "); i++) {

            if (gamesList[i].getPlayers().size() > 3) {

                id = gamesList[i].getId();

            }
        }


        String[] joinstate = chaturajiService.joinGame(id);

        // successfully joined a game
        assertEquals("Bad request", joinstate[1]);


    }

    @Test
    public void invalidGameId() throws Exception {

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        chaturajiService.register("test_invalid_game_id@test.com", "testpass123", "testman2");

        String state = chaturajiService.login("test_invalid_game_id@test.com", "testpass123");

        assertEquals("Success", state);

        Game[] gamesList = chaturajiService.getGames();

        // this is the test account that was previously registered, therefore should be able to log in
        assertNotNull(gamesList);

        String id = " ";

        String[] joinstate = chaturajiService.joinGame(id);

        // successfully joined a game
        assertEquals("Bad request", joinstate[1]);


    }

    @Test
    public void cannotJoinOwnGame() throws Exception {

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setServerHostAndPort(serverHostAndPort);

        String email = "test_join_own_game@test.com";

        chaturajiService.register(email, "testpass123", "testman2");

        String state = chaturajiService.login(email, "testpass123");

        assertEquals("Success", state);

        String[] state2 = chaturajiService.createGame("0");

        assertEquals("Success", state2[0]);

        Game[] gamesList = chaturajiService.getGames();

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


        String[] joinstate = chaturajiService.joinGame(id);

        // successfully joined a game
        assertEquals("Bad request", joinstate[1]);


    }

}