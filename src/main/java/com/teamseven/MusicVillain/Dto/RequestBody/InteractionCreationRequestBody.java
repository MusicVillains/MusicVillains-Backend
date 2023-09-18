package com.teamseven.MusicVillain.Dto.RequestBody;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InteractionCreationRequestBody {
    public String feedId;
    public String memberId;
}
