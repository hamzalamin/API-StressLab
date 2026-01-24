package com.wora.apistresslab.controllers;

import com.wora.apistresslab.models.DTOs.CreateLoadGeneratorDto;
import com.wora.apistresslab.models.DTOs.LoadTestResultDto;
import com.wora.apistresslab.services.ILoadGeneratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoadGeneratorController {

    private final ILoadGeneratorService service;


    @PostMapping("/load-test")
    public ResponseEntity<LoadTestResultDto> executeTestResult(@RequestBody @Valid CreateLoadGeneratorDto dto) {
        return new ResponseEntity<>(service.executeLoadTest(dto), HttpStatus.OK);
    }


}
