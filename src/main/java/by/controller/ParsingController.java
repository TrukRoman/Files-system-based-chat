package by.controller;

import by.model.Message;
import by.model.User;
import by.parser.ParserMessageHistoryToJSON;
import by.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class ParsingController {

    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    private ParserMessageHistoryToJSON parserMessageHistoryToJSON;

    @Autowired
    public void setParserMessageHistoryToJSON(ParserMessageHistoryToJSON parserMessageHistoryToJSON) {
        this.parserMessageHistoryToJSON = parserMessageHistoryToJSON;
    }

    @GetMapping("/parsing")
    public String parsingPage() {
        return "parsing";
    }

    @PostMapping("/parsing")
    public String parsingPage(Authentication authentication, @RequestParam("user_id") int userTo_id) throws IOException {
        User sender = (User) authentication.getPrincipal();
        List<Message> listHistory = messageService.getMessagesHistory(sender.getId(), userTo_id);

        parserMessageHistoryToJSON.parseListToJSON(listHistory);

        return "parsing";
    }
}
