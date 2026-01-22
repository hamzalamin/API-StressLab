package com.wora.apistresslab.mappers.api;

public interface Mapper<E, D> {
    E toEntity(D dto);
    D toDto(E entity);
}
