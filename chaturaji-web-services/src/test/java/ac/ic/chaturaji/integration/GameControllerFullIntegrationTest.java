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

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author samirarabbanian
 */
public class GameControllerFullIntegrationTest {

    protected static Tomcat tomcat;
    protected static KeyStore trustStore;
    protected static int numberOfGames;
    protected static int httpPort;
    protected static int httpsPort;

    @BeforeClass
    public static void setupFixture() throws Exception {
        System.setProperty("http.port", "" + PortFactory.findFreePort());
        httpPort = Integer.parseInt(System.getProperty("http.port"));

        System.setProperty("https.port", "" + PortFactory.findFreePort());
        httpsPort = Integer.parseInt(System.getProperty("https.port"));

        String classLocation = GameControllerFullIntegrationTest.class.getCanonicalName().replace(".", "/") + ".class";
        String projectBase = GameControllerFullIntegrationTest.class.getClassLoader().getResource(classLocation).toString().replace(classLocation, "../../").replace("file:", "");

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
        numberOfGames = 4;
    }

    @AfterClass
    public static void shutdownFixture() throws LifecycleException, InterruptedException {
        // stop server
        tomcat.stop();
    }

    protected void registerUser(String email, String password) throws Exception {
        HttpClient httpClient = createApacheClient();
        HttpPost register = new HttpPost("https://127.0.0.1:" + httpsPort + "/register");
        register.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("email", email),
                new BasicNameValuePair("nickname", "some_nickname"),
                new BasicNameValuePair("password", password)
        )));
        httpClient.execute(register);
    }

    protected CloseableHttpClient createApacheClient() throws Exception {
        return HttpClients.custom()
                // make sure the tests don't block when server fails to start up
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout((int) TimeUnit.SECONDS.toMillis(40000000))
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

    protected int numberOfGames() throws Exception {
        HttpClient httpClient = createApacheClient();
        HttpGet request = new HttpGet("https://127.0.0.1:" + httpsPort + "/games");
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        return new ObjectMapperFactory().createObjectMapper().readValue(EntityUtils.toString(entity), Game[].class).length;
    }
}
