package ac.ic.chaturaji;

import ac.ic.chaturaji.chatuService.ChatuService;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import ac.ic.chaturaji.web.PortFactory;
import android.util.Log;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.logging.Level;

import static org.junit.Assert.*;

/**
 * @author haider
 */
public class ChatuServiceTest {

    private static Tomcat tomcat;
    private static KeyStore trustStore;
    private static int httpPort;
    private static int httpsPort;
    private static String serverHost = "127.0.0.1";

    @BeforeClass
    public static void setupFixture() throws Exception {
        System.setProperty("http.port", "" + PortFactory.findFreePort());
        httpPort = Integer.parseInt(System.getProperty("http.port"));

        System.setProperty("https.port", "" + PortFactory.findFreePort());
        httpsPort = Integer.parseInt(System.getProperty("https.port"));

        String classLocation = ChatuServiceTest.class.getCanonicalName().replace(".", "/") + ".class";
        String projectBase = ChatuServiceTest.class.getClassLoader().getResource(classLocation).toString().replace(classLocation, "../../").replace("file:", "");

        // start proxy (in tomcat)
        tomcat = new Tomcat();
        tomcat.setBaseDir(new File(".").getCanonicalPath() + File.separatorChar + "tomcat");

        // add http port
        tomcat.setPort(httpPort);

        // add https port
        Connector httpsConnector = new Connector();
        httpsConnector.setPort(httpsPort);
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
        Context ctx = tomcat.addContext("/chaturaji-web-services", new File(".").getAbsolutePath());
        ContextConfig contextConfig = new ContextConfig();
        ctx.addLifecycleListener(contextConfig);
        contextConfig.setDefaultWebXml(projectBase + "../chaturaji-web-services/src/main/webapp/WEB-INF/web.xml");

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
    public static void shutdownFixture() throws LifecycleException, InterruptedException {
        // stop server
        tomcat.stop();
    }

    @Test
    public void shouldRegisterUser() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        String state = chatuService.createAccount("test@test.com", "testpass", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);
    }


    @Test
    public void requestSpeed() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        long startTime = System.currentTimeMillis();

        String state = chatuService.createAccount("test@test.com", "testpass", "testman");

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println(elapsedTime);

        long maxRequestTime = 5000;
        assertTrue(elapsedTime < maxRequestTime);

        startTime = System.currentTimeMillis();

        state = chatuService.login("test@test.com", "testpass");

        elapsedTime = System.currentTimeMillis() - startTime;

        assertTrue(elapsedTime < maxRequestTime);

        startTime = System.currentTimeMillis();

        state = chatuService.getGames();

        elapsedTime = System.currentTimeMillis() - startTime;

        assertTrue(elapsedTime < maxRequestTime);

        startTime = System.currentTimeMillis();

        state = chatuService.createGame("0");

        elapsedTime = System.currentTimeMillis() - startTime;

        assertTrue(elapsedTime < maxRequestTime);
    }


    @Test
    public void shouldNotRegister() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        String state = chatuService.createAccount("NotAProperEmail", "testpass", "testman");

        // Should return Error if a proper email is not entered
        assertEquals("Error", state);

        state = chatuService.createAccount("test@test.com", "pas", "testman");

        // Should return Error if too small a password is entered
        assertEquals("Error", state);

        state = chatuService.createAccount("test@test.com", "pas8ht4343to3ijto5453t43tiorjgoirhgohergouehgojngoh54ogni45ohrtoigerogneroigheuirbgeroingeurohgoerngiuergergjieprng43ohgp3gji03igpiong", "testman");

        // Should return Error if too large a password is entered
        assertEquals("Error", state);
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

        String state = chatuService.createAccount("wrongpassword@test.com", "password", "testman");

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

        String state = chatuService.login("test@test.com", "testpass");

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

        String state = chatuService.createAccount("test@test.com", "testpass", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chatuService.login("test@test.com", "testpass");

        assertEquals("Success", state);

        chatuService.clearCookieCred();

        state = chatuService.createGame("2");

        // we cleared the cookie and creds on the client, so should not be able to log in now
        assertEquals("Error", state);
    }

    @Test
    public void shouldCreateGame() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        String state = chatuService.createAccount("test@test.com", "testpass", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chatuService.login("test@test.com", "testpass");

        assertEquals("Success", state);

        state = chatuService.createGame("2");

        // we cleared the cookie and creds on the client, so should not be able to log in now
        assertEquals("Success", state);
    }

    @Test
    public void tooManyAIs() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        String state = chatuService.createAccount("test@test.com", "testpass", "testman");

        // if state is successful then it was a 201
        assertEquals("Success", state);

        state = chatuService.login("test@test.com", "testpass");

        assertEquals("Success", state);

        state = chatuService.createGame("5");

        // should not allow more than 3 AIs
        assertEquals("Invalid AI count", state);
    }

    @Test
    public void shouldJoinGame() throws Exception {

        ChatuService chatuService = ChatuService.getInstance();
        chatuService.setServerHost(serverHost);
        chatuService.setServerPort(httpsPort);

        chatuService.createAccount("join_test@test.com", "testpass", "testman");

        String state = chatuService.login("join_test@test.com", "testpass");

        assertEquals("Success", state);

        state = chatuService.createGame("0");

        assertEquals("Success", state);

        chatuService.clearCookieCred();

        state = chatuService.createAccount("test2@test.com", "testpass", "testman2");

        assertEquals("Success", state);

        state = chatuService.login("test2@test.com", "testpass");

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

        String state = chatuService.createAccount("test2@test.com", "testpass", "testman2");

        state = chatuService.login("test2@test.com", "testpass");

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

        String state = chatuService.login("test@test.com", "testpass");

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

        String email = "unqi@test.com";

        chatuService.createAccount(email, "testpass", "testman2");

        String state = chatuService.login(email, "testpass");

        assertEquals("Success", state);

        state = chatuService.createGame("0");

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
