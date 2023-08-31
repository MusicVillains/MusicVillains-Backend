package com.teamseven.MusicVillain.Dto.ResponseBody;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomResponseBody {
    /**
     * CustomResponseBody is used to keep the custom format of the response body<br>
     *
     * - statusCode, message are ignored using @JsonIgnore when response(by frontend's request)
     * @author Woody K
     * @see ResponseObject
     */
    @JsonIgnore
    public int statusCode;
    @JsonIgnore
    public String message;
    public Object data;
}

