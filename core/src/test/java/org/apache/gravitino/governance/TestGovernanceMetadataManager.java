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
package org.apache.gravitino.governance;

import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.gravitino.Config;
import org.apache.gravitino.GravitinoEnv;
import org.apache.gravitino.dto.governance.GovernanceMetadataDTO;
import org.apache.gravitino.dto.requests.GovernanceMetadataUpdateRequest;
import org.apache.gravitino.storage.relational.TestJDBCBackend;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestTemplate;

public class TestGovernanceMetadataManager extends TestJDBCBackend {
  private final GovernanceMetadataManager manager = new GovernanceMetadataManager();

  @AfterAll
  public static void restoreEnvironmentConfig() throws IllegalAccessException {
    FieldUtils.writeField(GravitinoEnv.getInstance(), "config", new Config(false) {}, true);
  }

  @TestTemplate
  public void testLifecycle() {
    GovernanceMetadataUpdateRequest initial =
        new GovernanceMetadataUpdateRequest(
            "Customer identifier",
            "Customer",
            Arrays.asList("pii", "key"),
            Collections.singletonList("Customer ID"));

    GovernanceMetadataDTO created =
        manager.upsert("metalake", "column", "catalog.schema.customers.id", initial);
    Assertions.assertEquals("COLUMN", created.getObjectType());
    Assertions.assertEquals("Customer", created.getDomain());
    Assertions.assertEquals(initial.getTags(), created.getTags());
    Assertions.assertEquals(
        initial.getGlossaryTerms(),
        manager
            .get("metalake", "COLUMN", "catalog.schema.customers.id")
            .orElseThrow(AssertionError::new)
            .getGlossaryTerms());

    GovernanceMetadataUpdateRequest replacement =
        new GovernanceMetadataUpdateRequest(
            "Stable customer key",
            "Sales",
            Collections.singletonList("certified"),
            Arrays.asList("Customer", "Identifier"));
    GovernanceMetadataDTO updated =
        manager.upsert("metalake", "COLUMN", "catalog.schema.customers.id", replacement);
    Assertions.assertEquals("Stable customer key", updated.getDescription());
    Assertions.assertEquals(Collections.singletonList("certified"), updated.getTags());

    Assertions.assertTrue(manager.delete("metalake", "COLUMN", "catalog.schema.customers.id"));
    Assertions.assertFalse(
        manager.get("metalake", "COLUMN", "catalog.schema.customers.id").isPresent());
    Assertions.assertFalse(manager.delete("metalake", "COLUMN", "catalog.schema.customers.id"));
  }

  @TestTemplate
  public void testRejectUnsupportedObjectType() {
    GovernanceMetadataUpdateRequest request =
        new GovernanceMetadataUpdateRequest(
            null, null, Collections.emptyList(), Collections.emptyList());
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> manager.upsert("metalake", "catalog", "catalog", request));
  }
}
