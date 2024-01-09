package io.valandur.webapi.world;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

public record CreateWorldData(
    @NotNull @Schema(requiredMode = RequiredMode.REQUIRED) String name,
    @NotNull @Schema(requiredMode = RequiredMode.REQUIRED) String type,
    Long seed,
    String difficulty
) {
}
