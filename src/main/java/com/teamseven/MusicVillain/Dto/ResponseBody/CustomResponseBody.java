package com.teamseven.MusicVillain.Dto.ResponseBody;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomResponseBody {
    public int statusCode;
    public String message;
    public Object data;
}

