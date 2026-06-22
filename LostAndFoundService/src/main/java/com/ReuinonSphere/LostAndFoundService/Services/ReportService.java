package com.ReuinonSphere.LostAndFoundService.Services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ReuinonSphere.LostAndFoundService.Dto.CloudinaryResponse;
import com.ReuinonSphere.LostAndFoundService.Dto.RegisterReportDto;
import com.ReuinonSphere.LostAndFoundService.Dto.ReportDto;
import com.ReuinonSphere.LostAndFoundService.Entity.Report;
import com.ReuinonSphere.LostAndFoundService.Exceptions.ReportNotFoundException;
import com.ReuinonSphere.LostAndFoundService.Repository.ReportRepo;
import com.ReuinonSphere.LostAndFoundService.Utils.EntityMappers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

     /**
      * Repository responsible for persisting and retrieving report documents
      * from MongoDB.
      */
     private final ReportRepo reportRepo;

     /**
      * Service responsible for uploading images to Cloudinary and returning
      * metadata such as secure URLs and public identifiers.
      */
     private final CloudinaryService cloudinaryService;

     /**
      * MapStruct mapper used for converting between DTOs and domain entities.
      */
     private final EntityMappers entityMappers;

     /**
      * Creates a new lost-and-found report.
      *
      * Workflow:
      * 1. Validate that at least one image is provided.
      * 2. Upload all images to Cloudinary.
      * 3. Store image URLs and public IDs in the report.
      * 4. Persist the report in MongoDB.
      *
      * @param registerReportDto report details submitted by the client
      * @param multipartFile     images associated with the report
      * @return persisted report data
      */
     public ReportDto createReport(RegisterReportDto registerReportDto, List<MultipartFile> multipartFile) {
          log.info("Recieved request to create ne report");
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
          log.info("Report created succesfully with id: {}", savedReport.getReportId());
          return entityMappers.toDto(savedReport);
     }

     /**
      * Updates an existing report.
      *
      * If new images are provided, they replace the existing image metadata.
      * Otherwise, existing image URLs and public IDs are preserved.
      *
      * @param reportDto     updated report information
      * @param multipartFile optional replacement images
      * @return updated report data
      * @throws ReportNotFoundException if the report does not exist
      */
     public ReportDto updateReport(ReportDto reportDto, List<MultipartFile> multipartFile) {
          log.info("Received request to update report with id={}",
                    reportDto.getReportId());

          Report existingReport = reportRepo.findById(reportDto.getReportId())
                    .orElseThrow(() -> {
                         log.warn("Update failed: Report not found with id={}",
                                   reportDto.getReportId());
                         return new ReportNotFoundException(
                                   "Report not found with Id " + reportDto.getReportId());
                    });

          if (multipartFile != null && !multipartFile.isEmpty()) {
               log.debug("Uploading {} replacement image(s) for report id={}",
                         multipartFile.size(),
                         reportDto.getReportId());
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
               log.debug("No new images provided. Preserving existing images for report id={}",
                         reportDto.getReportId());
               reportDto.setImageUrls(existingReport.getImageUrls());
               reportDto.setPublicId(existingReport.getPublicId());
          }

          Report savedReport = reportRepo.save(entityMappers.toEntity(reportDto));
          return entityMappers.toDto(savedReport);
     }

     /**
      * Retrieves reports using pagination to prevent loading the entire
      * dataset into memory.
      *
      * @param pageable pagination and sorting configuration
      * @return paginated report data
      */
     public Page<ReportDto> findAllReports(Pageable pageable) {
          log.info("Fetching reports. page={}, size={}, sort={}",
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    pageable.getSort());
          return reportRepo.findAll(pageable)
                    .map(entityMappers::toDto);
     }

     /**
      * Retrieves a report by its unique identifier.
      *
      * @param id report identifier
      * @return report data
      * @throws ReportNotFoundException if no report exists with the given id
      */
     public ReportDto findById(String id) {
          log.debug("Fetching report with id={}", id);
          Report report = reportRepo.findById(id)
                    .orElseThrow(() -> {
                         log.warn("Report not found with id={}", id);
                         return new ReportNotFoundException(
                                   "Report Not found with Id : " + id);
                    });

          log.debug("Successfully fetched report with id={}", id);
          return entityMappers.toDto(report);
     }

     /**
      * Deletes a single report.
      *
      * Existence is validated before deletion to provide a meaningful
      * business exception instead of silently ignoring the request.
      *
      * @param id report identifier
      * @throws ReportNotFoundException if the report does not exist
      */
     public void deleteReportById(String id) {
          log.info("Received request to delete report with id={}", id);
          if (!reportRepo.existsById(id)) {
               log.warn("Delete failed. Report not found with id={}", id);
               throw new ReportNotFoundException(
                         "Report Does not exists with id: " + id);
          }
          cloudinaryService.deleteProfileImages(reportRepo.findById(id).get().getPublicId());
          reportRepo.deleteById(id);
          log.info("Report deleted successfully with id={}", id);
     }

     /**
      * Deletes multiple reports in a single operation.
      *
      * Validation ensures that every requested report exists before any
      * deletion occurs, preventing partial data removal.
      *
      * @param ids collection of report identifiers
      * @throws ReportNotFoundException if one or more reports do not exist
      */
     public void deleteAllById(List<String> ids) {
          log.info("Received request to delete {} report(s)", ids.size());
          List<Report> reports = reportRepo.findAllById(ids);

          if (reports.size() != ids.size()) {
               log.warn("Bulk delete failed. Requested={} Found={}",
                         ids.size(),
                         reports.size());
               throw new ReportNotFoundException(
                         "One or more reports were not found");
          }
          for (Report report : reports) {
               cloudinaryService.deleteProfileImages(report.getPublicId());
          }
          reportRepo.deleteAllById(ids);
          log.info("Successfully deleted {} report(s)", ids.size());
     }

}
