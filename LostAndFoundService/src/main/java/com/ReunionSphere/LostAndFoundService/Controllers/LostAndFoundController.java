package com.ReunionSphere.LostAndFoundService.Controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ReunionSphere.LostAndFoundService.Dto.RegisterReportDto;
import com.ReunionSphere.LostAndFoundService.Dto.ReportDto;
import com.ReunionSphere.LostAndFoundService.Security.UserPrincipal;
import com.ReunionSphere.LostAndFoundService.Services.ReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
public class LostAndFoundController {
     // Injected LostReport Service
     private final ReportService reportService;

     // Fetching all reports (Public)
     @GetMapping
     public ResponseEntity<Page<ReportDto>> findAllReports(Pageable pageable) {
          log.info("Fetching all reports");
          return ResponseEntity.ok(reportService.findAllReports(pageable));
     }

     // Fetching user's own reports (Authenticated)
     @GetMapping("/my-reports")
     public ResponseEntity<Page<ReportDto>> findMyReports(
               @AuthenticationPrincipal UserPrincipal principal, 
               Pageable pageable) {
          log.info("Fetching reports for user: {}", principal.userId());
          return ResponseEntity.ok(reportService.findReportsByReporterId(principal.userId(), pageable));
     }

     // Fetching report by id (Public)
     @GetMapping("/{id}")
     public ResponseEntity<ReportDto> findById(@PathVariable String id) {
          log.info("Fetching report by id: {}", id);
          return ResponseEntity.ok(reportService.findById(id));
     }

     // Creating Report (Authenticated)
     @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     public ResponseEntity<ReportDto> createLostReport(
               @Valid @RequestPart(value = "reportDto", required = true) RegisterReportDto registerReportDto,
               @RequestPart(value = "images", required = true) List<MultipartFile> images,
               @AuthenticationPrincipal UserPrincipal principal) {
          
          log.info("Creating report for user: {}", principal.userId());
          // Auto-set reporterId from the authenticated token
          registerReportDto.setReporterId(principal.userId());
          
          return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReport(registerReportDto, images));
     }

     // Updating Report (Authenticated + Ownership check)
     @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     public ResponseEntity<ReportDto> updateLostReport(
               @Valid @RequestPart(value = "reportDto", required = true) ReportDto reportDto,
               @RequestPart(value = "images", required = false) List<MultipartFile> images,
               @AuthenticationPrincipal UserPrincipal principal) {
          
          log.info("Updating report: {} by user: {}", reportDto.getReportId(), principal.userId());
          return ResponseEntity.status(HttpStatus.OK).body(reportService.updateReport(reportDto, images, principal.userId()));
     }

     // Deleting report (Authenticated + Ownership check)
     @DeleteMapping("/{id}")
     public ResponseEntity<?> deleteReport(
               @PathVariable String id,
               @AuthenticationPrincipal UserPrincipal principal) {
          
          log.info("Deleting report: {} by user: {}", id, principal.userId());
          reportService.deleteReportById(id, principal.userId());
          return ResponseEntity.noContent().build();
     }

     // Deleting All report by ids (Authenticated + Ownership check)
     @DeleteMapping
     public ResponseEntity<?> deleteAllReports(
               @RequestBody List<String> reportIds,
               @AuthenticationPrincipal UserPrincipal principal) {
          
          log.info("Deleting multiple reports by user: {}", principal.userId());
          reportService.deleteAllById(reportIds, principal.userId());
          return ResponseEntity.noContent().build();
     }
}
