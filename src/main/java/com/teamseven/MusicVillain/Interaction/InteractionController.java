package com.teamseven.MusicVillain.Interaction;

import com.teamseven.MusicVillain.Interaction.RequestBodyForm.InteractionCreationRequestBody;
import com.teamseven.MusicVillain.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;
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
    public ResponseDto doInteraction(@RequestBody InteractionCreationRequestBody requestBody){
        System.out.println("[DEBUG] InteractionController.createInteraction");
        System.out.println("[DEBUG] feedId: " + requestBody.feedId);
        System.out.println("[DEBUG] memberId: " + requestBody.memberId);
        Map <String, String> resultMap =  interactionService.insertInteraction(requestBody);
        if (resultMap.get("result").equals("fail")){
            return new ResponseDto(400, "fail",null);
        }

        return new ResponseDto(200, "success", resultMap.get("interactionId"));
    }
}
