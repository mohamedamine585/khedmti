package com.safalifter.filestorage.controller;

import com.safalifter.filestorage.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/v1/file-storage")
@RequiredArgsConstructor
@Tag(name = "File Storage", description = "Upload, download and delete images")
public class StorageController {

    private final StorageService storageService;

    @Operation(
            summary = "Upload image",
            description = "Upload an image to file system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid file")
            }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImageToFIleSystem(
            @Parameter(
                    description = "Image file to upload",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart("image") MultipartFile file) {

        return ResponseEntity.ok(storageService.uploadImageToFileSystem(file));
    }

    @Operation(
            summary = "Download image",
            description = "Download image by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image returned",
                            content = @Content(mediaType = "image/png")),
                    @ApiResponse(responseCode = "404", description = "Image not found")
            }
    )
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadImageFromFileSystem(
            @Parameter(description = "Image ID") @PathVariable String id) {

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(storageService.downloadImageFromFileSystem(id));
    }

    @Operation(
            summary = "Delete image",
            description = "Delete image by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Image deleted"),
                    @ApiResponse(responseCode = "404", description = "Image not found")
            }
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteImageFromFileSystem(
            @Parameter(description = "Image ID") @PathVariable String id) {

        storageService.deleteImageFromFileSystem(id);
        return ResponseEntity.ok().build();
    }
}
