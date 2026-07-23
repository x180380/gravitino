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
package org.apache.gravitino.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.gravitino.rest.RESTRequest;

/** Request to replace the business governance metadata of an object. */
@Getter
@EqualsAndHashCode
@ToString
public class GovernanceMetadataUpdateRequest implements RESTRequest {

  @JsonProperty("description")
  private final String description;

  @JsonProperty("domain")
  private final String domain;

  @JsonProperty("tags")
  private final List<String> tags;

  @JsonProperty("glossaryTerms")
  private final List<String> glossaryTerms;

  /**
   * Creates an update request.
   *
   * @param description business description
   * @param domain business domain
   * @param tags tag names
   * @param glossaryTerms glossary term names
   */
  public GovernanceMetadataUpdateRequest(
      String description, String domain, List<String> tags, List<String> glossaryTerms) {
    this.description = description;
    this.domain = domain;
    this.tags = tags == null ? Collections.emptyList() : tags;
    this.glossaryTerms = glossaryTerms == null ? Collections.emptyList() : glossaryTerms;
  }

  /** Constructor used by Jackson. */
  public GovernanceMetadataUpdateRequest() {
    this(null, null, null, null);
  }

  @Override
  public void validate() {
    validateNames(tags, "tags");
    validateNames(glossaryTerms, "glossaryTerms");
    Preconditions.checkArgument(
        domain == null || StringUtils.isNotBlank(domain), "domain must not be empty");
  }

  private static void validateNames(List<String> names, String field) {
    Preconditions.checkNotNull(names, "%s must not be null", field);
    Preconditions.checkArgument(
        names.stream().allMatch(StringUtils::isNotBlank),
        "%s must not contain null or empty names",
        field);
    Preconditions.checkArgument(
        names.stream().distinct().count() == names.size(), "%s must not contain duplicates", field);
  }
}
