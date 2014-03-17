package ac.ic.chaturaji.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author samirarabbanian
 */
@Controller
public class MessageController {

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public String messagePage() {
        return "message";
    }
}
