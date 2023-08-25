package com.teamseven.MusicVillain.Utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MockMusicObject {
    String musicName;
    String musicianName;
}
