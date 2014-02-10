package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.dao.GameDAO;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author samirarabbanian
 */
@Controller
public class GameController {
    @Resource
    private GameDAO gameDAO;

    private ObjectMapper objectMapper = new ObjectMapper();


    @ResponseBody
    @RequestMapping(value = "/games", method = RequestMethod.GET)
    public String getGameList() throws IOException {

        String result = objectMapper.writeValueAsString(gameDAO.getAll());
        return result;
    }
}
