package com.mazyavr.schedule.dto;

import com.mazyavr.schedule.entity.TaskEntity;
import io.swagger.v3.oas.annotations.Operation;

import java.time.ZonedDateTime;

public record TaskDto(
        long id,
        String name,
        String description,
        Long priority,
        boolean status,
        ZonedDateTime start,
        ZonedDateTime end
) {
    @Operation(
            summary = "Создание задачи"
    )
    public static TaskDto fromEntity(TaskEntity entity) {
        return new TaskDto(entity.getId(), entity.getName(), entity.getDescription(), entity.getPriority(),
                entity.isStatus(), entity.getStart(), entity.getEnd());
    }
}
