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

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestGovernanceMetadataUpdateRequest {

  @Test
  public void testValidation() {
    GovernanceMetadataUpdateRequest request =
        new GovernanceMetadataUpdateRequest(
            "Customer master data",
            "Customer",
            Arrays.asList("pii", "gold"),
            Collections.singletonList("Customer ID"));
    Assertions.assertDoesNotThrow(request::validate);

    GovernanceMetadataUpdateRequest duplicateTags =
        new GovernanceMetadataUpdateRequest(
            null, null, Arrays.asList("pii", "pii"), Collections.emptyList());
    Assertions.assertThrows(IllegalArgumentException.class, duplicateTags::validate);

    GovernanceMetadataUpdateRequest blankTerm =
        new GovernanceMetadataUpdateRequest(
            null, null, Collections.emptyList(), Collections.singletonList(" "));
    Assertions.assertThrows(IllegalArgumentException.class, blankTerm::validate);
  }
}
