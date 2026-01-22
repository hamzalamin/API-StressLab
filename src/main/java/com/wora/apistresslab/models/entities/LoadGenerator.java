package com.wora.apistresslab.models.entities;


import com.wora.apistresslab.models.enums.ExecutionStatus;
import com.wora.apistresslab.models.enums.HttpMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "load_generators")
public class LoadGenerator {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 2048)
    private String url;

    @NotNull
    @Min(value = 1)
    @Column(nullable = false)
    private Integer requestNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HttpMethod httpMethod;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus executionStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime executedAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (executionStatus == null) {
            executionStatus = ExecutionStatus.PENDING;
        }
    }
}
