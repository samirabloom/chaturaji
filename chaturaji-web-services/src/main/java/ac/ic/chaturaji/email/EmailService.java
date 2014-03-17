package ac.ic.chaturaji.email;

import ac.ic.chaturaji.model.User;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author samirarabbanian
 */
@Component
public class EmailService {

    public static final String CHATURAJI_PREFIX = "Chaturaji - ";
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private Environment environment;
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    public void sendRegistrationMessage(User user, HttpServletRequest request) throws MalformedURLException, UnsupportedEncodingException {
        String subject = CHATURAJI_PREFIX + "New Registration";
        String formattedMessage = "<html><head><title>" + subject + "</title></head><body>\n" +
                "<h1>" + subject + "</h1>\n" +
                "<p>A new user has just been registered for " + user.getEmail() + "</p>\n" +
                "</body></html>";
        sendMessage(environment.getProperty("email.contact.address"), new String[]{user.getEmail()}, subject, formattedMessage);
    }

    public void sendUpdatePasswordMessage(User user, HttpServletRequest request) throws MalformedURLException, UnsupportedEncodingException {
        URL link = createUrl(user, request);
        String subject = CHATURAJI_PREFIX + "Update Password";
        String formattedMessage = "<html><head><title>" + subject + "</title></head><body>\n" +
                "<h1>" + subject + "</h1>\n" +
                "<p>To update your password please click on the following link <a href=" + link + ">" + link + "</a></p>\n" +
                "</body></html>";
        sendMessage(environment.getProperty("email.contact.address"), new String[]{user.getEmail()}, subject, formattedMessage);
    }

    private void sendMessage(final String from, final String[] to, final String subject, final String msg) {
        taskExecutor.execute(new SendMessageTask(from, to, subject, msg, mailSender));
    }

    private URL createUrl(User user, HttpServletRequest request) throws MalformedURLException, UnsupportedEncodingException {
        String hostHeader = request.getHeader("Host");
        String host = "ec2-54-186-2-140.us-west-2.compute.amazonaws.com";
        int port = request.getLocalPort();
        if (!Strings.isNullOrEmpty(hostHeader)) {
            if (hostHeader.contains(":")) {
                host = StringUtils.substringBefore(hostHeader, ":");
                try {
                    port = Integer.parseInt(StringUtils.substringAfterLast(hostHeader, ":"));
                } catch (NumberFormatException nfe) {
                    logger.warn("NumberFormatException parsing port from Host header [" + hostHeader + "]", nfe);
                }
            } else {
                host = hostHeader;
            }
        }
        return new URL(
                "https",
                host,
                port,
                "/updatePassword?email=" + URLEncoder.encode(user.getEmail(), "UTF-8") + "&oneTimeToken=" + URLEncoder.encode(user.getOneTimeToken(), "UTF-8")
        );
    }

    public class SendMessageTask implements Runnable {
        private final String from;
        private final String[] to;
        private final String subject;
        private final String msg;
        private final JavaMailSender mailSender;

        public SendMessageTask(String from, String[] to, String subject, String msg, JavaMailSender mailSender) {
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.msg = msg;
            this.mailSender = mailSender;
        }

        public void run() {
            try {
                mailSender.send(new MimeMessagePreparator() {
                    public void prepare(MimeMessage mimeMessage) throws MessagingException {
                        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
                        message.setFrom(from);
                        message.setTo(to);
                        message.setSubject(subject);
                        message.setText(msg, true);
                    }
                });
            } catch (Exception e) {
                logger.warn("Failed to send " + subject + "email to " + to, e);
            }
        }

    }
}
