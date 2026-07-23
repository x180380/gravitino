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
package org.apache.gravitino.storage.relational.mapper.provider.postgresql;

import static org.apache.gravitino.storage.relational.mapper.GovernanceMetadataMapper.TABLE_NAME;

import org.apache.gravitino.storage.relational.mapper.provider.base.GovernanceMetadataBaseSQLProvider;
import org.apache.gravitino.storage.relational.po.GovernanceMetadataPO;

/** PostgreSQL SQL provider for governance metadata. */
public class GovernanceMetadataPostgreSQLProvider extends GovernanceMetadataBaseSQLProvider {

  @Override
  public String upsert(GovernanceMetadataPO metadata) {
    return "INSERT INTO "
        + TABLE_NAME
        + " (metalake_name, object_type, full_name, description, domain_name, tags,"
        + " glossary_terms, updated_at) VALUES (#{metadata.metalakeName},"
        + " #{metadata.objectType}, #{metadata.fullName}, #{metadata.description},"
        + " #{metadata.domainName}, #{metadata.tags}, #{metadata.glossaryTerms},"
        + " #{metadata.updatedAt}) ON CONFLICT (metalake_name, object_type, full_name)"
        + " DO UPDATE SET description = EXCLUDED.description,"
        + " domain_name = EXCLUDED.domain_name, tags = EXCLUDED.tags,"
        + " glossary_terms = EXCLUDED.glossary_terms, updated_at = EXCLUDED.updated_at";
  }
}
