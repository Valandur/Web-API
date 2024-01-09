package io.valandur.webapi.world;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.List;

public record WorldConstants(
    @GraphQLNonNull
    @Schema(requiredMode = RequiredMode.REQUIRED) List<String> types,
    @GraphQLNonNull
    @Schema(requiredMode = RequiredMode.REQUIRED) List<String> difficulties
) {

}
