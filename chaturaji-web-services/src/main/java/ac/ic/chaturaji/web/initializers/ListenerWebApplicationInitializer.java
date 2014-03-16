package ac.ic.chaturaji.web.initializers;

import ac.ic.chaturaji.web.controller.InMemoryGamesContextListener;
import ac.ic.chaturaji.web.websockets.WebSocketServletContextListener;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class ListenerWebApplicationInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        servletContext.addListener(new WebSocketServletContextListener());
        servletContext.addListener(new InMemoryGamesContextListener());
    }

}
