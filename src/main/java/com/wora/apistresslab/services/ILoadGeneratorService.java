package com.wora.apistresslab.services;

import com.wora.apistresslab.models.DTOs.CreateLoadGeneratorDto;
import com.wora.apistresslab.models.DTOs.LoadTestResultDto;

public interface ILoadGeneratorService {
    public LoadTestResultDto executeLoadTest(CreateLoadGeneratorDto createLoadGeneratorDto);

    public LoadTestResultDto executeConcurrentLoadTest(CreateLoadGeneratorDto createLoadGeneratorDto);
}
