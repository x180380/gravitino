/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.gravitino.server.web.rest;

import static org.apache.gravitino.governance.GovernanceMetadataManager.DOMAIN_DEFINITION;
import static org.apache.gravitino.governance.GovernanceMetadataManager.GLOSSARY_DEFINITION;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.gravitino.dto.requests.GovernanceDefinitionRequest;
import org.apache.gravitino.dto.responses.BaseResponse;
import org.apache.gravitino.dto.responses.GovernanceDefinitionListResponse;
import org.apache.gravitino.dto.responses.GovernanceDefinitionResponse;
import org.apache.gravitino.governance.GovernanceMetadataManager;
import org.apache.gravitino.server.web.Utils;

/** REST API for reusable business domain and glossary term definitions. */
@Path("/metalakes/{metalake}/governance/{kind}")
public class GovernanceDefinitionOperations {
  private final GovernanceMetadataManager manager;

  @Context private HttpServletRequest httpRequest;

  /**
   * Creates governance definition operations.
   *
   * @param manager governance metadata manager
   */
  @Inject
  public GovernanceDefinitionOperations(GovernanceMetadataManager manager) {
    this.manager = manager;
  }

  /** Lists definitions. */
  @GET
  @Produces("application/vnd.gravitino.v1+json")
  public Response list(@PathParam("metalake") String metalake, @PathParam("kind") String kind) {
    try {
      String type = definitionType(kind);
      return Utils.doAs(
          httpRequest,
          () ->
              Utils.ok(
                  new GovernanceDefinitionListResponse(manager.listDefinitions(metalake, type))));
    } catch (IllegalArgumentException e) {
      return Utils.illegalArguments(e.getMessage(), e);
    } catch (Exception e) {
      return Utils.internalError(e.getMessage(), e);
    }
  }

  /** Creates or replaces a definition. */
  @PUT
  @Produces("application/vnd.gravitino.v1+json")
  public Response upsert(
      @PathParam("metalake") String metalake,
      @PathParam("kind") String kind,
      GovernanceDefinitionRequest request) {
    try {
      String type = definitionType(kind);
      return Utils.doAs(
          httpRequest,
          () ->
              Utils.ok(
                  new GovernanceDefinitionResponse(
                      manager.upsertDefinition(metalake, type, request))));
    } catch (IllegalArgumentException e) {
      return Utils.illegalArguments(e.getMessage(), e);
    } catch (Exception e) {
      return Utils.internalError(e.getMessage(), e);
    }
  }

  /** Deletes a definition by name. */
  @DELETE
  @Path("/{name}")
  @Produces("application/vnd.gravitino.v1+json")
  public Response delete(
      @PathParam("metalake") String metalake,
      @PathParam("kind") String kind,
      @PathParam("name") String name) {
    try {
      String type = definitionType(kind);
      return Utils.doAs(
          httpRequest,
          () ->
              manager.deleteDefinition(metalake, type, name)
                  ? Utils.ok(new BaseResponse())
                  : Utils.notFound(
                      "NoSuchGovernanceDefinitionException",
                      "Governance definition does not exist: " + name));
    } catch (IllegalArgumentException e) {
      return Utils.illegalArguments(e.getMessage(), e);
    } catch (Exception e) {
      return Utils.internalError(e.getMessage(), e);
    }
  }

  private static String definitionType(String kind) {
    if ("domains".equalsIgnoreCase(kind)) {
      return DOMAIN_DEFINITION;
    }
    if ("glossary-terms".equalsIgnoreCase(kind)) {
      return GLOSSARY_DEFINITION;
    }
    throw new IllegalArgumentException("kind must be domains or glossary-terms");
  }
}
