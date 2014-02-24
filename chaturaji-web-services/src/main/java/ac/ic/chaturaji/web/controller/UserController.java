package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.dao.UserDAO;
import ac.ic.chaturaji.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author samirarabbanian
 */
@Controller
public class UserController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private UserDAO userDAO;
    @Resource
    private PasswordEncoder passwordEncoder;

    @ResponseBody
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> register(@Valid User user, BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (int i = 0; i < fieldErrors.size(); i++) {
                errorMessage
                        .append(fieldErrors.get(i).getField())
                        .append(" - ")
                        .append(fieldErrors.get(i).getDefaultMessage());
                if (i < fieldErrors.size() - 1) {
                    errorMessage.append("\n");
                }
            }
            return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
        }
        try {
            userDAO.save(new User(UUID.randomUUID().toString(), user.getEmail(), passwordEncoder.encode(user.getPassword()), user.getNickname()));
        } catch (Exception e) {
            logger.warn("Exception while saving game", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> login() {
        return new ResponseEntity<>("", HttpStatus.ACCEPTED);
    }

}
