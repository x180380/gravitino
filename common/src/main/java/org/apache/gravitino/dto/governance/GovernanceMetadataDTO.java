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
package org.apache.gravitino.dto.governance;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Business governance metadata attached to a database, table, or column. */
@Getter
@EqualsAndHashCode
@ToString
public class GovernanceMetadataDTO {

  @JsonProperty("objectType")
  private final String objectType;

  @JsonProperty("fullName")
  private final String fullName;

  @JsonProperty("description")
  private final String description;

  @JsonProperty("domain")
  private final String domain;

  @JsonProperty("tags")
  private final List<String> tags;

  @JsonProperty("glossaryTerms")
  private final List<String> glossaryTerms;

  @JsonProperty("updatedAt")
  private final long updatedAt;

  /**
   * Creates governance metadata.
   *
   * @param objectType object type
   * @param fullName object full name
   * @param description business description
   * @param domain business domain
   * @param tags tag names
   * @param glossaryTerms glossary term names
   * @param updatedAt last update time in milliseconds since the epoch
   */
  public GovernanceMetadataDTO(
      String objectType,
      String fullName,
      String description,
      String domain,
      List<String> tags,
      List<String> glossaryTerms,
      long updatedAt) {
    this.objectType = objectType;
    this.fullName = fullName;
    this.description = description;
    this.domain = domain;
    this.tags = tags == null ? Collections.emptyList() : tags;
    this.glossaryTerms = glossaryTerms == null ? Collections.emptyList() : glossaryTerms;
    this.updatedAt = updatedAt;
  }

  /** Constructor used by Jackson. */
  public GovernanceMetadataDTO() {
    this(null, null, null, null, null, null, 0L);
  }
}
