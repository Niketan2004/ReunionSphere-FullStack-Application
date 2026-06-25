package com.ReuinonSphere.LostAndFoundService.Utils;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import com.ReuinonSphere.LostAndFoundService.Dto.LocationDto;
import com.ReuinonSphere.LostAndFoundService.Dto.RegisterReportDto;
import com.ReuinonSphere.LostAndFoundService.Dto.ReportDto;
import com.ReuinonSphere.LostAndFoundService.Entity.Report;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntityMappers {

     ReportDto toDto(Report report);

     ReportDto registerReportToDto(RegisterReportDto registerReportDto);

     Report toEntity(ReportDto reportDto);

     void updateEntityFromDto(ReportDto dto, @MappingTarget Report entity);

     default GeoJsonPoint map(LocationDto locationDto) {
          if (locationDto == null) {
               return null;
          }

          return new GeoJsonPoint(
                    locationDto.getLongitude(),
                    locationDto.getLatitude());
     }

     default LocationDto map(GeoJsonPoint point) {
          if (point == null) {
               return null;
          }

          return new LocationDto(
                    point.getX(),
                    point.getY());
     }
}