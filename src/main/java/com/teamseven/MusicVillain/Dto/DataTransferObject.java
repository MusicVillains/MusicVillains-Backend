package com.teamseven.MusicVillain.Dto;
import java.util.List;

public interface DataTransferObject<Entity, Dto> {
    Dto toDto(Entity entity);
    List<Dto> toDtoList(List<Entity> entityList);

}
