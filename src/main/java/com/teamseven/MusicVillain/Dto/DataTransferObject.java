package com.teamseven.MusicVillain.Dto;
import java.util.List;

public interface DataTransferObject<Entity, DTO> {
    DTO toDto(Entity entity);
    List<DTO> toDtoList(List<Entity> entityList);

}
