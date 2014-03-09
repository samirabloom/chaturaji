package ac.ic.chaturaji.web.websockets;

import ac.ic.chaturaji.websockets.WebSocketsServer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author samirarabbanian
 */
public class WebSocketServletContextListener implements ServletContextListener {

    public static final String WEB_SOCKET_CLIENT_ATTRIBUTE_NAME = "WEB_SOCKET_CLIENT_ATTRIBUTE_NAME";
    private WebSocketsServer webSocketsServer = new WebSocketsServer();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        webSocketsServer.startServer(9090);
        servletContextEvent.getServletContext().setAttribute(WEB_SOCKET_CLIENT_ATTRIBUTE_NAME, webSocketsServer.getClients());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        webSocketsServer.stopServer();
    }
}
