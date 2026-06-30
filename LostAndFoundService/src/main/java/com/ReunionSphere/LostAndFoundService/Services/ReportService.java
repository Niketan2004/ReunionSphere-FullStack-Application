package com.ReunionSphere.LostAndFoundService.Services;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ReunionSphere.LostAndFoundService.Dto.CloudinaryResponse;
import com.ReunionSphere.LostAndFoundService.Dto.RegisterReportDto;
import com.ReunionSphere.LostAndFoundService.Dto.ReportDto;
import com.ReunionSphere.LostAndFoundService.Entity.Report;
import com.ReunionSphere.LostAndFoundService.Exceptions.ReportNotFoundException;
import com.ReunionSphere.LostAndFoundService.Exceptions.UnauthorizedAccessException;
import com.ReunionSphere.LostAndFoundService.Repository.ReportRepo;
import com.ReunionSphere.LostAndFoundService.Utils.EntityMappers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

     private final ReportRepo reportRepo;
     private final CloudinaryService cloudinaryService;
     private final EntityMappers entityMappers;

     @CacheEvict(value = "reports-pages", allEntries = true)
     public ReportDto createReport(RegisterReportDto registerReportDto, List<MultipartFile> multipartFile) {
          log.info("Received request to create new report for user: {}", registerReportDto.getReporterId());
          if (multipartFile == null || multipartFile.isEmpty()) {
               log.warn("Report creation failed: No Images were provided");
               throw new RuntimeException("Minimum one image should be provided!");
          }

          log.debug("Uploading {} image(s) to cloudinary", multipartFile.size());
          ReportDto reportDto = entityMappers.registerReportToDto(registerReportDto);
          List<CloudinaryResponse> cloudinaryResponse = cloudinaryService.uploadImages(multipartFile);

          reportDto.setImageUrls(cloudinaryResponse
                    .stream()
                    .map(CloudinaryResponse::getSecureUrl)
                    .toList());
          reportDto.setPublicId(cloudinaryResponse
                    .stream()
                    .map(CloudinaryResponse::getPublicId)
                    .toList());

          Report savedReport = reportRepo.insert(entityMappers.toEntity(reportDto));
          log.info("Report created successfully with id: {}", savedReport.getReportId());
          return entityMappers.toDto(savedReport);
     }

     @Caching(put = {
               @CachePut(value = "reports", key = "#reportDto.reportId")
     }, evict = {
               @CacheEvict(value = "reports-pages", allEntries = true)
     })
     public ReportDto updateReport(ReportDto reportDto, List<MultipartFile> multipartFile, String requestingUserId) {
          log.info("Received request to update report with id={} by user={}", reportDto.getReportId(), requestingUserId);

          Report existingReport = reportRepo.findById(reportDto.getReportId())
                    .orElseThrow(() -> {
                         log.warn("Update failed: Report not found with id={}", reportDto.getReportId());
                         return new ReportNotFoundException("Report not found with Id " + reportDto.getReportId());
                    });

          // Ownership validation
          if (!existingReport.getReporterId().equals(requestingUserId)) {
               log.warn("Unauthorized update attempt on report {} by user {}", reportDto.getReportId(), requestingUserId);
               throw new UnauthorizedAccessException("You are not authorized to update this report.");
          }

          // Ensure reporterId cannot be changed
          reportDto.setReporterId(existingReport.getReporterId());

          if (multipartFile != null && !multipartFile.isEmpty()) {
               log.debug("Uploading {} replacement image(s) for report id={}", multipartFile.size(), reportDto.getReportId());
               List<CloudinaryResponse> cloudinaryResponse = cloudinaryService.uploadImages(multipartFile);
               reportDto.setImageUrls(
                         cloudinaryResponse.stream()
                                   .map(CloudinaryResponse::getSecureUrl)
                                   .toList());
               reportDto.setPublicId(
                         cloudinaryResponse.stream()
                                   .map(CloudinaryResponse::getPublicId)
                                   .toList());
          } else {
               log.debug("No new images provided. Preserving existing images for report id={}", reportDto.getReportId());
               reportDto.setImageUrls(existingReport.getImageUrls());
               reportDto.setPublicId(existingReport.getPublicId());
          }
          
          entityMappers.updateEntityFromDto(reportDto, existingReport);
          Report savedReport = reportRepo.save(existingReport);
          return entityMappers.toDto(savedReport);
     }

     @Cacheable(value = "reports-pages", key = "#pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort")
     public Page<ReportDto> findAllReports(Pageable pageable) {
          log.info("Fetching reports. page={}, size={}, sort={}",
                    pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
          return reportRepo.findAll(pageable)
                    .map(entityMappers::toDto);
     }

     public Page<ReportDto> findReportsByReporterId(String reporterId, Pageable pageable) {
          log.info("Fetching reports for reporterId={}, page={}, size={}", 
                    reporterId, pageable.getPageNumber(), pageable.getPageSize());
          return reportRepo.findByReporterId(reporterId, pageable)
                    .map(entityMappers::toDto);
     }

     @Cacheable(value = "reports", key = "#id")
     public ReportDto findById(String id) {
          log.debug("Fetching report with id={}", id);
          Report report = reportRepo.findById(id)
                    .orElseThrow(() -> {
                         log.warn("Report not found with id={}", id);
                         return new ReportNotFoundException("Report Not found with Id : " + id);
                    });
          return entityMappers.toDto(report);
     }

     @Caching(evict = {
               @CacheEvict(value = "reports", key = "#id"),
               @CacheEvict(value = "reports-pages", allEntries = true)
     })
     public void deleteReportById(String id, String requestingUserId) {
          log.info("Received request to delete report with id={} by user={}", id, requestingUserId);
          Report report = reportRepo.findById(id)
                    .orElseThrow(() -> {
                         log.warn("Delete failed. Report not found with id={}", id);
                         return new ReportNotFoundException("Report Does not exists with id: " + id);
                    });

          // Ownership validation
          if (!report.getReporterId().equals(requestingUserId)) {
               log.warn("Unauthorized delete attempt on report {} by user {}", id, requestingUserId);
               throw new UnauthorizedAccessException("You are not authorized to delete this report.");
          }

          cloudinaryService.deleteProfileImages(report.getPublicId());
          reportRepo.deleteById(id);
          log.info("Report deleted successfully with id={}", id);
     }

     @Caching(evict = {
               @CacheEvict(value = "reports", allEntries = true),
               @CacheEvict(value = "reports-pages", allEntries = true)
     })
     public void deleteAllById(List<String> ids, String requestingUserId) {
          log.info("Received request to delete {} report(s) by user={}", ids.size(), requestingUserId);
          List<Report> reports = reportRepo.findAllById(ids);

          if (reports.size() != ids.size()) {
               log.warn("Bulk delete failed. Requested={} Found={}", ids.size(), reports.size());
               throw new ReportNotFoundException("One or more reports were not found");
          }

          // Ownership validation for all requested reports
          for (Report report : reports) {
               if (!report.getReporterId().equals(requestingUserId)) {
                    log.warn("Unauthorized bulk delete attempt on report {} by user {}", report.getReportId(), requestingUserId);
                    throw new UnauthorizedAccessException("You are not authorized to delete one or more of these reports.");
               }
          }

          for (Report report : reports) {
               cloudinaryService.deleteProfileImages(report.getPublicId());
          }
          reportRepo.deleteAllById(ids);
          log.info("Successfully deleted {} report(s)", ids.size());
     }
}
