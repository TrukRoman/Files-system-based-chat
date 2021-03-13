package by.controller;

import by.parser.ParserToJSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
public class ParsingController {

    private ParserToJSON parserToJSON;

    @Autowired
    private void setParserToJSON(ParserToJSON parserToJSON) {
        this.parserToJSON = parserToJSON;
    }

    @GetMapping("/parsing")
    public String parsingPage() {
        return "parsing";
    }

    @PostMapping("/parsing")
    public String parsingPage(String sender, String recipient) throws IOException {
        parserToJSON.saveJsonFile(sender, recipient);
        return "parsing";
    }
}
