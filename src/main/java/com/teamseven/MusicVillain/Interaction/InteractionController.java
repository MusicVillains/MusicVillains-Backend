package com.teamseven.MusicVillain.Interaction;

import com.teamseven.MusicVillain.Interaction.RequestBodyForm.InteractionCreationRequestBody;
import com.teamseven.MusicVillain.ResponseDto;
import com.teamseven.MusicVillain.ResponseObject;
import com.teamseven.MusicVillain.ServiceResult;
import com.teamseven.MusicVillain.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
public class InteractionController {

    private final InteractionService interactionService;

    @Autowired
    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping("/interaction")
    public ResponseObject doInteraction(@RequestBody InteractionCreationRequestBody requestBody){

        ServiceResult result =  interactionService.insertInteraction(requestBody);
        return result.isFailed() ? ResponseObject.of(Status.BAD_REQUEST, result.getData())
                : ResponseObject.of(Status.OK, result.getData());
    }

    @GetMapping("/interaction/count")
    // url: /interaction/count?feedId={feedId}
    public ResponseObject getInteractionCountByFeedId(@RequestParam("feedId") String feedId){
        ServiceResult result = interactionService.getInteractionCountByFeedId(feedId);
        return result.isFailed() ? ResponseObject.of(Status.BAD_REQUEST, result.getData())
                : ResponseObject.of(Status.OK, result.getData());
    }

}
