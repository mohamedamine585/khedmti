package com.safalifter.jobservice.controller;

import com.safalifter.jobservice.dto.JobDto;
import com.safalifter.jobservice.request.job.JobCreateRequest;
import com.safalifter.jobservice.request.job.JobUpdateRequest;
import com.safalifter.jobservice.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/job-service/job")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    private final ModelMapper modelMapper;


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create job with optional file",
            description = "Send job name and category as request parameters, and optional file as multipart/form-data"
    )
    public ResponseEntity<JobDto> createJob(
            @Parameter(description = "Job name", required = true)
            @RequestParam("name") String name,

            @Parameter(description = "Category ID", required = true)
            @RequestParam("categoryId") String categoryId,

            @Parameter(description = "Optional file", schema = @Schema(type = "string", format = "binary"))
            @RequestPart(value = "file", required = false) MultipartFile file) {

        // Map request params to DTO
        JobCreateRequest request = new JobCreateRequest();
        request.setName(name);
        request.setCategoryId(categoryId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(jobService.createJob(request, file), JobDto.class));
    }


    @PostMapping("/getJobsThatFitYourNeeds/{needs}")
    ResponseEntity<List<JobDto>> getJobsThatFitYourNeeds(@PathVariable String needs) {
        return ResponseEntity.ok(jobService.getJobsThatFitYourNeeds(needs).stream()
                .map(job -> modelMapper.map(job, JobDto.class)).toList());
    }

    @GetMapping("/getAll")
    ResponseEntity<List<JobDto>> getAll() {
        return ResponseEntity.ok(jobService.getAll().stream()
                .map(job -> modelMapper.map(job, JobDto.class)).toList());
    }

    @GetMapping("/getJobById/{id}")
    ResponseEntity<JobDto> getJobById(@PathVariable String id) {
        return ResponseEntity.ok(modelMapper.map(jobService.getJobById(id), JobDto.class));
    }

    @GetMapping("/getJobsByCategoryId/{id}")
    ResponseEntity<List<JobDto>> getJobsByCategoryId(@PathVariable String id) {
        return ResponseEntity.ok(jobService.getJobsByCategoryId(id).stream()
                .map(job -> modelMapper.map(job, JobDto.class)).toList());
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update job with optional file",
            description = "Send job name and category as request parameters, and optional file as multipart/form-data"
    )
    public ResponseEntity<JobDto> updateJob(
            @Parameter(description = "Job name", required = true)
            @RequestParam("name") String name,

            @Parameter(description = "Category ID", required = true)
            @RequestParam("categoryId") String categoryId,

            @Parameter(description = "Optional file", schema = @Schema(type = "string", format = "binary"))
            @RequestPart(value = "file", required = false) MultipartFile file) {

        // Map request params to DTO
        JobUpdateRequest request = new JobUpdateRequest();
        request.setName(name);
        request.setCategoryId(categoryId);

        return ResponseEntity.ok(modelMapper.map(jobService.updateJob(request, file), JobDto.class));
    }

    @DeleteMapping("/deleteJobById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> deleteJobById(@PathVariable String id) {
        jobService.deleteJobById(id);
        return ResponseEntity.ok().build();
    }
}
