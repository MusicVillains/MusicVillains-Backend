package com.teamseven.MusicVillain.Dto.RequestBody;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteractionCreationRequestBody {
    public String feedId;
    public String memberId;
}
