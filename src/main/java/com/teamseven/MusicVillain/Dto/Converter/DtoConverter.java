package com.teamseven.MusicVillain.Dto.Converter;
import com.teamseven.MusicVillain.Dto.DataTransferObject;
import java.util.List;

public interface DtoConverter<Entity, Dto extends DataTransferObject> {
    Dto convertToDto(Entity entity);
    List<Dto> convertToDtoList(List<Entity> entityList);

}