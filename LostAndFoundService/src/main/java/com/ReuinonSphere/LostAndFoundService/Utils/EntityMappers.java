package com.ReuinonSphere.LostAndFoundService.Utils;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import com.ReuinonSphere.LostAndFoundService.Dto.RegisterReportDto;
import com.ReuinonSphere.LostAndFoundService.Dto.ReportDto;
import com.ReuinonSphere.LostAndFoundService.Entity.Report;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntityMappers {
     // ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);
     // From Report to Dto
     ReportDto toDto(Report report);

     // From Register report Dto to Report Dto
     ReportDto registerReportToDto(RegisterReportDto registerReportDto);

     Report toEntity(ReportDto reportDto);

     void updateEntityFromDto(ReportDto dto, @MappingTarget Report entity);

}
