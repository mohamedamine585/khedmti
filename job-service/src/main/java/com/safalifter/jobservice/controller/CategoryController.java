package com.safalifter.jobservice.controller;

import com.safalifter.jobservice.dto.CategoryDto;
import com.safalifter.jobservice.dto.JobDto;
import com.safalifter.jobservice.request.category.CategoryCreateRequest;
import com.safalifter.jobservice.request.category.CategoryUpdateRequest;
import com.safalifter.jobservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
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
@RequestMapping("/v1/job-service/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create category with optional file",
            description = "Send category name as request parameter + optional image as multipart/form-data"
    )
    public ResponseEntity<CategoryDto> createCategory(
            @Parameter(description = "Category name", required = true)
            @RequestParam("name") String name, // now name is a request param

            @Parameter(description = "Image file", schema = @Schema(type = "string", format = "binary"))
            @RequestPart(value = "file", required = false) MultipartFile file) {

        // Map name to DTO
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setName(name);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(categoryService.createCategory(request, file), CategoryDto.class));
    }



    @GetMapping("/getAll")
    ResponseEntity<List<CategoryDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAll().stream()
                .map(category -> modelMapper.map(category, CategoryDto.class)).toList());
    }

    @GetMapping("/getCategoryById/{id}")
    ResponseEntity<CategoryDto> getCategoryById(@PathVariable String id) {
        return ResponseEntity.ok(modelMapper.map(categoryService.getCategoryById(id), CategoryDto.class));
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JobDto> updateCategoryById(
            @Parameter(description = "Category ID to update", required = true)
            @RequestParam("id") String id,

            @Parameter(description = "Category name", required = true)
            @RequestParam("name") String name,

            @Parameter(description = "Optional file", schema = @Schema(type = "string", format = "binary"))
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {

        // Map request parameters to DTO
        CategoryUpdateRequest request = new CategoryUpdateRequest();
        request.setId(id);
        request.setName(name);

        return ResponseEntity.ok(
                modelMapper.map(categoryService.updateCategoryById(request, file), JobDto.class)
        );
    }

    @DeleteMapping("/deleteCategoryById/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> deleteCategoryById(@PathVariable String id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.ok().build();
    }
}
