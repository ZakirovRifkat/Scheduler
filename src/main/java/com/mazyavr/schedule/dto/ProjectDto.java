package com.mazyavr.schedule.dto;

import com.mazyavr.schedule.entity.ProjectEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProjectDto(
        @Schema(description = "ID")
        long id,
        @Schema(description = "Название")
        String name

) {
    @Operation(
            summary = "Создаёт проект"
    )
    public static ProjectDto fromEntity(ProjectEntity entity) {
        return new ProjectDto(entity.getId(), entity.getName());
    }
}
