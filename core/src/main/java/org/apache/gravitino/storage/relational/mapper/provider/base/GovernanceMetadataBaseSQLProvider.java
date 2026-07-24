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
package org.apache.gravitino.storage.relational.mapper.provider.base;

import static org.apache.gravitino.storage.relational.mapper.GovernanceMetadataMapper.TABLE_NAME;

import org.apache.gravitino.storage.relational.po.GovernanceMetadataPO;

/** SQL shared by MySQL and H2 for governance metadata. */
public class GovernanceMetadataBaseSQLProvider {

  /** Returns SQL for selecting one object. */
  public String select(String metalakeName, String objectType, String fullName) {
    return "SELECT metalake_name AS metalakeName, object_type AS objectType,"
        + " full_name AS fullName, description, domain_name AS domainName, tags,"
        + " glossary_terms AS glossaryTerms, updated_at AS updatedAt FROM "
        + TABLE_NAME
        + " WHERE metalake_name = #{metalakeName} AND object_type = #{objectType}"
        + " AND full_name = #{fullName}";
  }

  /** Returns SQL for listing one definition type. */
  public String list(String metalakeName, String objectType) {
    return "SELECT metalake_name AS metalakeName, object_type AS objectType,"
        + " full_name AS fullName, description, domain_name AS domainName, tags,"
        + " glossary_terms AS glossaryTerms, updated_at AS updatedAt FROM "
        + TABLE_NAME
        + " WHERE metalake_name = #{metalakeName} AND object_type = #{objectType}"
        + " ORDER BY full_name";
  }

  /** Returns SQL for replacing one object. */
  public String upsert(GovernanceMetadataPO metadata) {
    return "INSERT INTO "
        + TABLE_NAME
        + " (metalake_name, object_type, full_name, description, domain_name, tags,"
        + " glossary_terms, updated_at) VALUES (#{metadata.metalakeName},"
        + " #{metadata.objectType}, #{metadata.fullName}, #{metadata.description},"
        + " #{metadata.domainName}, #{metadata.tags}, #{metadata.glossaryTerms},"
        + " #{metadata.updatedAt}) ON DUPLICATE KEY UPDATE"
        + " description = #{metadata.description}, domain_name = #{metadata.domainName},"
        + " tags = #{metadata.tags}, glossary_terms = #{metadata.glossaryTerms},"
        + " updated_at = #{metadata.updatedAt}";
  }

  /** Returns SQL for deleting one object. */
  public String delete(String metalakeName, String objectType, String fullName) {
    return "DELETE FROM "
        + TABLE_NAME
        + " WHERE metalake_name = #{metalakeName} AND object_type = #{objectType}"
        + " AND full_name = #{fullName}";
  }
}
