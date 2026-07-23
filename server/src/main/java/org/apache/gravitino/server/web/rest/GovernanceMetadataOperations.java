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

import com.codahale.metrics.annotation.ResponseMetered;
import com.codahale.metrics.annotation.Timed;
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
import org.apache.gravitino.dto.governance.GovernanceMetadataDTO;
import org.apache.gravitino.dto.requests.GovernanceMetadataUpdateRequest;
import org.apache.gravitino.dto.responses.BaseResponse;
import org.apache.gravitino.dto.responses.GovernanceMetadataResponse;
import org.apache.gravitino.governance.GovernanceMetadataManager;
import org.apache.gravitino.metrics.MetricNames;
import org.apache.gravitino.server.web.Utils;

/** REST API for independently stored business governance metadata. */
@Path("/metalakes/{metalake}/governance/objects/{type}/{fullName}")
public class GovernanceMetadataOperations {
  private final GovernanceMetadataManager manager;

  @Context private HttpServletRequest httpRequest;

  /**
   * Creates governance metadata operations.
   *
   * @param manager governance metadata manager
   */
  @Inject
  public GovernanceMetadataOperations(GovernanceMetadataManager manager) {
    this.manager = manager;
  }

  /** Gets the governance metadata attached to an object. */
  @GET
  @Produces("application/vnd.gravitino.v1+json")
  @Timed(name = "get-governance-metadata." + MetricNames.HTTP_PROCESS_DURATION, absolute = true)
  @ResponseMetered(name = "get-governance-metadata", absolute = true)
  public Response get(
      @PathParam("metalake") String metalake,
      @PathParam("type") String type,
      @PathParam("fullName") String fullName) {
    try {
      return Utils.doAs(
          httpRequest,
          () ->
              manager
                  .get(metalake, type, fullName)
                  .<Response>map(value -> Utils.ok(new GovernanceMetadataResponse(value)))
                  .orElseGet(
                      () ->
                          Utils.notFound(
                              "NoSuchGovernanceMetadataException",
                              "Governance metadata does not exist for " + type + ": " + fullName)));
    } catch (IllegalArgumentException e) {
      return Utils.illegalArguments(e.getMessage(), e);
    } catch (Exception e) {
      return Utils.internalError(e.getMessage(), e);
    }
  }

  /** Creates or completely replaces the governance metadata attached to an object. */
  @PUT
  @Produces("application/vnd.gravitino.v1+json")
  @Timed(name = "upsert-governance-metadata." + MetricNames.HTTP_PROCESS_DURATION, absolute = true)
  @ResponseMetered(name = "upsert-governance-metadata", absolute = true)
  public Response upsert(
      @PathParam("metalake") String metalake,
      @PathParam("type") String type,
      @PathParam("fullName") String fullName,
      GovernanceMetadataUpdateRequest request) {
    try {
      return Utils.doAs(
          httpRequest,
          () -> {
            GovernanceMetadataDTO metadata = manager.upsert(metalake, type, fullName, request);
            return Utils.ok(new GovernanceMetadataResponse(metadata));
          });
    } catch (IllegalArgumentException e) {
      return Utils.illegalArguments(e.getMessage(), e);
    } catch (Exception e) {
      return Utils.internalError(e.getMessage(), e);
    }
  }

  /** Deletes the governance metadata attached to an object. */
  @DELETE
  @Produces("application/vnd.gravitino.v1+json")
  @Timed(name = "delete-governance-metadata." + MetricNames.HTTP_PROCESS_DURATION, absolute = true)
  @ResponseMetered(name = "delete-governance-metadata", absolute = true)
  public Response delete(
      @PathParam("metalake") String metalake,
      @PathParam("type") String type,
      @PathParam("fullName") String fullName) {
    try {
      return Utils.doAs(
          httpRequest,
          () ->
              manager.delete(metalake, type, fullName)
                  ? Utils.ok(new BaseResponse())
                  : Utils.notFound(
                      "NoSuchGovernanceMetadataException",
                      "Governance metadata does not exist for " + type + ": " + fullName));
    } catch (IllegalArgumentException e) {
      return Utils.illegalArguments(e.getMessage(), e);
    } catch (Exception e) {
      return Utils.internalError(e.getMessage(), e);
    }
  }
}
