package io.valandur.webapi.world;

import io.leangen.graphql.annotations.GraphQLQuery;

public class Block {

  @GraphQLQuery(name = "type", description = "The type of this block")
  public String type;

  public Block() {
  }

  public Block(String type) {
    this.type = type;
  }
}
