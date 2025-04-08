package com.example.winehood.mapper;

import com.example.winehood.config.MapperConfig;
import com.example.winehood.dto.region.CreateRegionRequestDto;
import com.example.winehood.dto.region.RegionDto;
import com.example.winehood.model.Region;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface RegionMapper {
    RegionDto toDto(Region region);

    Region toEntity(CreateRegionRequestDto requestDto);

    List<RegionDto> toDtoList(List<Region> regions);

    void updateEntityFromDto(
            CreateRegionRequestDto requestDto,
            @MappingTarget Region region);
}
