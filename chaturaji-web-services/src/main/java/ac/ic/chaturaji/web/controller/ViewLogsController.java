package ac.ic.chaturaji.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * @author samirarabbanian
 */
@Controller
public class ViewLogsController {

    @ResponseBody
    @RequestMapping(value = "/log", method = RequestMethod.GET)
    public String messagePage(@RequestParam(value = "lines", required = false) int numberOfLines) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("/var/log/chaturaji/server.log"));
        String rawLine;
        StringBuilder stringBuilder = new StringBuilder("<html><head></head><body>");

        LinkedList<String> lines = new LinkedList<>();
        while ((rawLine = reader.readLine()) != null) {
            lines.addFirst(rawLine);
        }

        if (numberOfLines == 0) {
            numberOfLines = 200;
        }
        for (int i = 0; i < numberOfLines; i++) {
            String line = lines.get(i);
            stringBuilder.append("<p>");
            stringBuilder.append(line);
            stringBuilder.append("</p>");
        }

        stringBuilder.append("</body>");

        return stringBuilder.toString();
    }
}
