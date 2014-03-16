package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.model.Game;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author samirarabbanian
 */
public class InMemoryGamesContextListener implements ServletContextListener {

    public static final String IN_MEMORY_GAMES_ATTRIBUTE_NAME = "IN_MEMORY_GAMES_ATTRIBUTE_NAME";

    public synchronized static Map<String, Game> getInMemoryGames(ServletContext servletContext) {
        if (servletContext.getAttribute(IN_MEMORY_GAMES_ATTRIBUTE_NAME) == null) {
            servletContext.setAttribute(IN_MEMORY_GAMES_ATTRIBUTE_NAME, new ConcurrentHashMap<String, Game>());
        }
        return (Map<String, Game>) servletContext.getAttribute(IN_MEMORY_GAMES_ATTRIBUTE_NAME);
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if (servletContextEvent.getServletContext().getAttribute(IN_MEMORY_GAMES_ATTRIBUTE_NAME) == null) {
            servletContextEvent.getServletContext().setAttribute(IN_MEMORY_GAMES_ATTRIBUTE_NAME, new ConcurrentHashMap<String, Game>());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
