package com.teamseven.MusicVillain.Interaction;

import com.teamseven.MusicVillain.Dto.RequestBody.InteractionCreationRequestBody;
import com.teamseven.MusicVillain.Dto.ResponseBody.ResponseObject;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Dto.ResponseBody.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class InteractionController {

    private final InteractionService interactionService;

    @Autowired
    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping("/interactions")
    public ResponseObject doInteraction(@RequestBody InteractionCreationRequestBody requestBody){

        ServiceResult result =  interactionService.insertInteraction(requestBody);
        return result.isFailed() ? ResponseObject.of(Status.BAD_REQUEST, result.getData())
                : ResponseObject.of(Status.OK, result.getData());
    }

    @GetMapping("/interactions/count")
    // url: /interaction/count?feedId={feedId}
    public ResponseObject getInteractionCountByFeedId(@RequestParam("feedId") String feedId){

        ServiceResult result = interactionService.getInteractionCountByFeedId(feedId);

        return result.isFailed() ? ResponseObject.of(Status.BAD_REQUEST, result.getData())
                : ResponseObject.of(Status.OK, result.getData());
    }

}
