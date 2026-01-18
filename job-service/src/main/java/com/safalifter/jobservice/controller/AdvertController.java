package com.safalifter.jobservice.controller;

import com.safalifter.jobservice.dto.AdvertDto;
import com.safalifter.jobservice.enums.Advertiser;
import com.safalifter.jobservice.request.advert.AdvertCreateRequest;
import com.safalifter.jobservice.request.advert.AdvertUpdateRequest;
import com.safalifter.jobservice.service.AdvertService;
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
@RequestMapping("/v1/job-service/advert")
@RequiredArgsConstructor
public class AdvertController {
    private final AdvertService advertService;
    private final ModelMapper modelMapper;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdvertDto> createAdvert(
            @Parameter(description = "Advert name", required = true)
            @RequestParam("name") String name,

            @Parameter(description = "Delivery time (days)", required = true)
            @RequestParam("deliveryTime") int deliveryTime,

            @Parameter(description = "Price", required = true)
            @RequestParam("price") int price,

            @Parameter(description = "Advertiser", required = true)
            @RequestParam("advertiser") Advertiser advertiser,

            @Parameter(description = "User ID", required = true)
            @RequestParam("userId") String userId,

            @Parameter(description = "Job ID", required = true)
            @RequestParam("jobId") String jobId,

            @Parameter(description = "Optional file", schema = @Schema(type = "string", format = "binary"))
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {

        // Map request params to DTO
        AdvertCreateRequest request = new AdvertCreateRequest();
        request.setName(name);
        request.setDeliveryTime(deliveryTime);
        request.setPrice(price);
        request.setAdvertiser(advertiser);
        request.setUserId(userId);
        request.setJobId(jobId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(advertService.createAdvert(request, file), AdvertDto.class));
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<AdvertDto>> getAll() {
        return ResponseEntity.ok(advertService.getAll().stream()
                .map(advert -> modelMapper.map(advert, AdvertDto.class)).toList());
    }

    @GetMapping("/getAdvertById/{id}")
    public ResponseEntity<AdvertDto> getAdvertById(@PathVariable String id) {
        return ResponseEntity.ok(modelMapper.map(advertService.getAdvertById(id), AdvertDto.class));
    }

    @GetMapping("/getAdvertsByUserId/{id}")
    public ResponseEntity<List<AdvertDto>> getAdvertsByUserId(@PathVariable String id,
                                                              @RequestParam Advertiser type) {
        return ResponseEntity.ok(advertService.getAdvertsByUserId(id, type).stream()
                .map(advert -> modelMapper.map(advert, AdvertDto.class)).toList());
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or @advertService.authorizeCheck(#id, principal)")
    public ResponseEntity<AdvertDto> updateAdvertById(
            @Parameter(description = "Advert ID to update", required = true)
            @RequestParam("id") String id,

            @Parameter(description = "Advert name", required = true)
            @RequestParam("name") String name,

            @Parameter(description = "Delivery time (days)", required = true)
            @RequestParam("deliveryTime") int deliveryTime,

            @Parameter(description = "Price", required = true)
            @RequestParam("price") int price,

            @Parameter(description = "Advertiser", required = true)
            @RequestParam("advertiser") Advertiser advertiser,

            @Parameter(description = "User ID", required = true)
            @RequestParam("userId") String userId,

            @Parameter(description = "Job ID", required = true)
            @RequestParam("jobId") String jobId,

            @Parameter(description = "Optional file", schema = @Schema(type = "string", format = "binary"))
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        // Map request parameters to DTO
        AdvertUpdateRequest request = new AdvertUpdateRequest();
        request.setId(id);
        request.setName(name);
        request.setDeliveryTime(deliveryTime);
        request.setPrice(price);


        return ResponseEntity.ok(
                modelMapper.map(advertService.updateAdvertById(request, file), AdvertDto.class)
        );
    }


    @DeleteMapping("/deleteAdvertById/{id}")
    @PreAuthorize("hasRole('ADMIN') or @advertService.authorizeCheck(#id, principal)")
    public ResponseEntity<Void> deleteAdvertById(@PathVariable String id) {
        advertService.deleteAdvertById(id);
        return ResponseEntity.ok().build();
    }
}
