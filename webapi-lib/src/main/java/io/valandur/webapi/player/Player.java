package io.valandur.webapi.player;

import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import io.valandur.webapi.world.Location;
import io.valandur.webapi.item.ItemStack;
import java.util.UUID;

@GraphQLNonNull
public record Player(
    @GraphQLNonNull
    @GraphQLQuery(description = uuidDescr)
    @Schema(description = uuidDescr, requiredMode = RequiredMode.REQUIRED)
    UUID uuid,
    @GraphQLNonNull
    @GraphQLQuery(description = nameDescr)
    @Schema(description = nameDescr, requiredMode = RequiredMode.REQUIRED)
    String name,
    @GraphQLNonNull
    @GraphQLQuery(description = locationDescr)
    @Schema(description = locationDescr, requiredMode = RequiredMode.REQUIRED)
    Location location,
    @GraphQLNonNull
    @GraphQLQuery(description = addressDescr)
    @Schema(description = addressDescr, requiredMode = RequiredMode.REQUIRED)
    String address,
    @GraphQLQuery(description = helmetDescr)
    @Schema(description = helmetDescr)
    ItemStack helmet,
    @GraphQLQuery(description = chestplateDescr)
    @Schema(description = chestplateDescr)
    ItemStack chestplate,
    @GraphQLQuery(description = leggingsDescr)
    @Schema(description = leggingsDescr)
    ItemStack leggings,
    @GraphQLQuery(description = bootsDescr)
    @Schema(description = bootsDescr)
    ItemStack boots) {

  private static final String uuidDescr = "The UUID of this player";
  private static final String nameDescr = "The name of this player";
  private static final String locationDescr = "The location of this player (world & position)";
  private static final String addressDescr = "The remote address of the player";
  private static final String helmetDescr = "The item that is in the helmet slot of this inventory";
  private static final String chestplateDescr = "The item that is in the chestplate slot of this inventory";
  private static final String leggingsDescr = "The item that is in the leggings slot of this inventory";
  private static final String bootsDescr = "The item that is in the boots slot of this inventory";

}
