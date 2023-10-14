package com.teamseven.MusicVillain.Dto;

import lombok.Data;

/**
 * @apiNote
 * 프론트 요청으로 좋아요 렌더링 시간 성능 개선을 위해 Feed 조회 API에 interactionProps 필드를 추가적으로 반환한다.<br>
 * interactionProps는 프론트쪽의 Interaction 수행을 위한 컴포넌트와 관련된 속성 정보를 담기 위한 객체이다.<br>
 */
@Data
public class interactionProps {
    String content;
    String backgroundColor;
    String border;
}
