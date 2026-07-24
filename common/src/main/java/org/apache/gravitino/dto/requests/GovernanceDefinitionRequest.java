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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.gravitino.rest.RESTRequest;

/** Request for creating or replacing a governance definition. */
@Getter
@NoArgsConstructor
public class GovernanceDefinitionRequest implements RESTRequest {
  @JsonProperty("name")
  private String name;

  @JsonProperty("description")
  private String description;

  /**
   * Creates a request.
   *
   * @param name definition name
   * @param description optional description
   */
  public GovernanceDefinitionRequest(String name, String description) {
    this.name = name;
    this.description = description;
  }

  @Override
  public void validate() {
    Preconditions.checkArgument(StringUtils.isNotBlank(name), "name must not be empty");
    Preconditions.checkArgument(name.length() <= 256, "name must not exceed 256 characters");
  }
}
