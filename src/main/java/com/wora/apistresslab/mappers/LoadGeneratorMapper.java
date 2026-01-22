package com.wora.apistresslab.mappers;

import com.wora.apistresslab.mappers.api.Mapper;
import com.wora.apistresslab.models.DTOs.CreateLoadGeneratorDto;
import com.wora.apistresslab.models.DTOs.LoadGeneratorDto;
import com.wora.apistresslab.models.entities.LoadGenerator;
import com.wora.apistresslab.models.enums.ExecutionStatus;
import org.springframework.stereotype.Component;

@Component
public class LoadGeneratorMapper implements Mapper<LoadGenerator, LoadGeneratorDto> {

    @Override
    public LoadGeneratorDto toDto(LoadGenerator entity) {

        if (entity == null) {
            return null;
        }

        return new LoadGeneratorDto(
                entity.getId(),
                entity.getUrl(),
                entity.getRequestNumber(),
                entity.getHttpMethod(),
                entity.getExecutionStatus(),
                entity.getCreatedAt(),
                entity.getExecutedAt()
        );
    }

    @Override
    public LoadGenerator toEntity(LoadGeneratorDto dto) {

        if (dto == null) {
            return null;
        }

        return LoadGenerator.builder()
                .id(dto.id())
                .url(dto.url())
                .requestNumber(dto.RequestNumber())
                .httpMethod(dto.httpMethod())
                .executionStatus(dto.executionStatus())
                .createdAt(dto.createdAt())
                .executedAt(dto.executedAt())
                .build();
    }

    public LoadGenerator toEntity(CreateLoadGeneratorDto dto) {

        if (dto == null) {
            return null;
        }

        return LoadGenerator.builder()
                .url(dto.url())
                .requestNumber(dto.RequestNumber())
                .httpMethod(dto.httpMethod())
                .executionStatus(ExecutionStatus.PENDING)
                .build();
    }

}
