package by.controller;

import by.service.MessageService;
import by.model.Message;
import by.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class ChatController {

    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping(value = "/chat")
    public String getUserByID(Authentication authentication, @RequestParam("userTo_id") int userTo_id,
                              @RequestParam("text") String text, Model model) throws IOException {

        User sender = (User) authentication.getPrincipal();

        messageService.save(new Message(sender.getId(), userTo_id, text));

        List<Message> listHistory = messageService.getMessagesHistory(sender.getId(), userTo_id);
        model.addAttribute("listHistory", listHistory);
        return "chat";
    }
}
