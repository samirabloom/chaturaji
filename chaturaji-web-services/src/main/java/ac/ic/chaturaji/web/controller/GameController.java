package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.dao.GameDAO;
import ac.ic.chaturaji.model.Game;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

/**
 * @author samirarabbanian
 */
@Controller
public class GameController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private GameDAO gameDAO;

    private ObjectMapper objectMapper = new ObjectMapper();


    @ResponseBody
    @RequestMapping(value = "/games", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String getGameList() throws IOException {

        return objectMapper.writeValueAsString(gameDAO.getAll());
    }

    @ResponseBody
    @RequestMapping(value = "/game", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> createGame(@RequestParam("numberOfAIPlayers") int numberOfAIPlayers) throws IOException {
        if (numberOfAIPlayers < 0 || numberOfAIPlayers > 3) {
            return new ResponseEntity<>("Invalid numberOfAIPlayers: " + numberOfAIPlayers + " is not between 0 and 3 inclusive", HttpStatus.BAD_REQUEST);
        }
        try {
            gameDAO.save(new Game(UUID.randomUUID().toString()));
        } catch (Exception e) {
            logger.warn("Exception while saving game", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }


}
