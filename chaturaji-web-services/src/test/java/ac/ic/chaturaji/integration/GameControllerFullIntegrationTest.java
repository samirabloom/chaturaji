package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import ac.ic.chaturaji.web.PortFactory;
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
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author samirarabbanian
 */
public class GameControllerFullIntegrationTest {

    protected static Server server;
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

        server = new Server();
        // add connectors
        server.addConnector(createHTTPConnector(server, httpPort, httpsPort));
        server.addConnector(createHTTPSConnector(projectBase, server, httpsPort));
        WebAppContext root = new WebAppContext();

        root.setWar(projectBase + "src/main/webapp");
        root.setContextPath("/");

        server.setHandler(root);
        server.start();

        // load key store for certificates
        trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fileInputStream = new FileInputStream(new File(projectBase + "../keystore"))) {
            trustStore.load(fileInputStream, "changeit".toCharArray());
        }
        numberOfGames = 4;
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

    protected void registerUser(String email, String password) throws Exception {
        HttpClient httpClient = createApacheClient();
        HttpPost register = new HttpPost("https://127.0.0.1:" + httpsPort + "/register");
        register.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("email", email),
                new BasicNameValuePair("nickname", "some_nickname"),
                new BasicNameValuePair("password", password)
        )));
        HttpResponse httpResponse = httpClient.execute(register);
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
