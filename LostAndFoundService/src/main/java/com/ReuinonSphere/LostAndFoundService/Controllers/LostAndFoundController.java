package com.ReuinonSphere.LostAndFoundService.Controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import com.ReuinonSphere.LostAndFoundService.Dto.RegisterReportDto;
import com.ReuinonSphere.LostAndFoundService.Dto.ReportDto;
import com.ReuinonSphere.LostAndFoundService.Services.ReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class LostAndFoundController {
     // Injected LostReport Service
     private final ReportService reportService;

     // Fetching all reports
     @GetMapping
     public ResponseEntity<Page<ReportDto>> findAllReports(Pageable pageable) {
          return ResponseEntity.ok(reportService.findAllReports(pageable));
     }

     // Fetching report by id
     @GetMapping("/{id}")
     public ResponseEntity<ReportDto> findById(@PathVariable String id) {
          return ResponseEntity.ok(reportService.findById(id));
     }

     // Creating Report
     @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     public ResponseEntity<ReportDto> createLostReport(
               @Valid @RequestPart(value = "reportDto", required = true) RegisterReportDto registerReportDto,
               @RequestPart(value = "images", required = true) List<MultipartFile> images) {
          return ResponseEntity.status(HttpStatus.CREATED).body(reportService.createReport(registerReportDto, images));
     }

     // Updating Report
     @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     public ResponseEntity<ReportDto> updateLostReport(
               @Valid @RequestPart(value = "reportDto", required = true) ReportDto reportDto,
               @RequestPart(value = "images", required = false) List<MultipartFile> images) {
          return ResponseEntity.status(HttpStatus.OK).body(reportService.updateReport(reportDto, images));
     }

     // Deleting report
     @DeleteMapping("/{id}")
     public ResponseEntity<?> deleteReport(@PathVariable String id) {
          reportService.deleteReportById(id);
          return ResponseEntity.noContent().build();
     }

     // Deleting All report by id
     @DeleteMapping
     public ResponseEntity<?> deleteAllReports(@RequestBody List<String> reportIds) {
          reportService.deleteAllById(reportIds);
          return ResponseEntity.noContent().build();
     }
}
