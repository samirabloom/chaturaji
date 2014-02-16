package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.dao.GameDAO;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.ServiceResponse;
import ac.ic.chaturaji.model.ServiceResult;
import org.codehaus.jackson.map.ObjectMapper;
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
    public String createGame(@RequestParam("numberOfAIPlayers") int numberOfAIPlayers) throws IOException {

        gameDAO.save(new Game(UUID.randomUUID().toString()));
        return objectMapper.writeValueAsString(new ServiceResponse(ServiceResult.SUCCESS, ""));
    }


}
