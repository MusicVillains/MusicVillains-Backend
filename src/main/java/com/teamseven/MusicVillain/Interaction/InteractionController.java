package com.teamseven.MusicVillain.Interaction;

import com.teamseven.MusicVillain.Interaction.RequestBodyForm.InteractionCreationRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
public class InteractionController {

    private final InteractionService interactionService;

    @Autowired
    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping("/interaction")
    @ResponseBody
    public String createInteraction(@RequestBody InteractionCreationRequestBody requestBody){
        System.out.println("[DEBUG] InteractionController.createInteraction");
        System.out.println("[DEBUG] feedId: " + requestBody.feedId);
        System.out.println("[DEBUG] memberId: " + requestBody.memberId);
        interactionService.insertInteraction(requestBody);
        return "interaction created";
    }
}
