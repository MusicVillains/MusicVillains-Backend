package com.teamseven.MusicVillain.Dto;


/**
 * 계층간 데이터 교환을 위한 객체(DTO, Data Transfer Object)를 표현하기 위한 마커 인터페이스로서,<br>
 * 해당 인터페이스의 구현체는 DTO로 사용되는 객체임을 의미한다.
 *
 * @param <OriginalType> DTO가 다루는 데이터 원본 타입<br>
 *                      * 예를 들어 `Feed` Entity를 운반하는 `FeedDto` 객체는 `Feed` 를 OriginalType으로 갖는다.<br>
 *
 */
public interface DataTransferObject<OriginalType> {

}
