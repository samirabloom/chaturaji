package ac.ic.chaturaji.security;

import ac.ic.chaturaji.dao.UserDAO;
import ac.ic.chaturaji.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author samirarabbanian
 */
@Component
public class CredentialValidation {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserDAO userDAO;

    public User validateUsernameAndPassword(String username, CharSequence password) {
        User user = userDAO.findByEmail(decodeURL(username));
        if (!credentialsMatch(password, user)) {
            logger.info("Invalid username and password combination");
            throw new BadCredentialsException("Invalid username and password combination");
        }
        return user;
    }

    private boolean credentialsMatch(CharSequence password, User user) {
        return user != null && password != null && user.getPassword() != null && passwordEncoder.matches(password, user.getPassword());
    }

    private String decodeURL(String urlEncoded) {
        try {
            return URLDecoder.decode(urlEncoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Exception decoding URL value [" + urlEncoded + "]", e);
        }
    }
}
