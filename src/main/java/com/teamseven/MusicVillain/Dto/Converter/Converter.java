package com.teamseven.MusicVillain.Dto.Converter;

import java.util.List;

public interface Converter<S, T> {
    T convert(S source);
    List<T> convertList(List<S> source);
}