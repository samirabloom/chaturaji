package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.dao.UserDAO;
import ac.ic.chaturaji.email.EmailService;
import ac.ic.chaturaji.model.User;
import ac.ic.chaturaji.security.SpringSecurityUserContext;
import ac.ic.chaturaji.uuid.UUIDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author samirarabbanian
 */
@Controller
public class UpdatePasswordController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final Pattern PASSWORD_MATCHER = Pattern.compile(User.PASSWORD_PATTERN);
    @Resource
    private UserDAO userDAO;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private SpringSecurityUserContext securityUserContext;
    @Resource
    private EmailService emailService;
    @Resource
    private UUIDFactory uuidService;

    @RequestMapping(value = "/sendUpdatePasswordEmail", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json; charset=UTF-8")
    public ResponseEntity sendUpdatePasswordEmail(String email, HttpServletRequest request) throws MalformedURLException, UnsupportedEncodingException {
        try {
            User user = userDAO.findByEmail(email);
            if (user != null) {
                user.setOneTimeToken(uuidService.generateUUID());
                userDAO.save(user);
                emailService.sendUpdatePasswordMessage(user, request);
            }
            logger.info("An email has been sent to " + email + " with a link to create your password and login");
            return new ResponseEntity<>("An email has been sent to " + email + " with a link to create your password and login", HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean hasInvalidToken(User user, String oneTimeToken, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        if (!uuidService.hasMatchingUUID(user, oneTimeToken)) {
            redirectAttributes.addFlashAttribute("message", "Invalid email or one-time-token" + (user != null ? " - click <a href=\"/sendUpdatePasswordEmail?email=" + URLEncoder.encode(user.getEmail(), "UTF-8") + "\">resend email</a> to receive a new email" : ""));
            redirectAttributes.addFlashAttribute("title", "Invalid Request");
            redirectAttributes.addFlashAttribute("error", true);
            return true;
        }
        return false;
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.GET)
    public String updatePasswordForm(String email, String oneTimeToken, Model uiModel, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        if (hasInvalidToken(userDAO.findByEmail(email), oneTimeToken, redirectAttributes)) {
            return "redirect:/message";
        }
        uiModel.addAttribute("email", email);
        uiModel.addAttribute("oneTimeToken", oneTimeToken);
        return "updatePassword";
    }

    @Transactional
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String email, String password, String passwordConfirm, String oneTimeToken, Model uiModel, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        User user = userDAO.findByEmail(email);
        if (hasInvalidToken(user, oneTimeToken, redirectAttributes)) {
            return "redirect:/message";
        }
        boolean passwordFormatError = !PASSWORD_MATCHER.matcher(String.valueOf(password)).matches();
        boolean passwordsMatchError = !String.valueOf(password).equals(passwordConfirm);
        if (passwordFormatError || passwordsMatchError) {
            uiModel.addAttribute("email", email);
            uiModel.addAttribute("oneTimeToken", oneTimeToken);
            List<String> errors = new ArrayList<>();
            if (passwordFormatError) {
                errors.add("Please provide a password of 8 or more characters with at least 1 digit and 1 letter");
            }
            if (passwordsMatchError) {
                errors.add("The second password field does not match the first password field");
            }
            uiModel.addAttribute("validationErrors", errors);
            logger.info("Validation error while trying to update password for " + email + "\n" + errors);
            return "updatePassword";
        }
        user.setPassword(passwordEncoder.encode(password));
        userDAO.save(user);
        securityUserContext.setCurrentUser(user);

        redirectAttributes.addFlashAttribute("message", "Your password has been updated");
        redirectAttributes.addFlashAttribute("title", "Password Updated");
        logger.info("Password updated for " + email);
        return "redirect:/message";
    }
}
