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
package org.apache.gravitino.storage.relational.mapper;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.gravitino.storage.relational.JDBCBackend;
import org.apache.gravitino.storage.relational.mapper.provider.base.GovernanceMetadataBaseSQLProvider;
import org.apache.gravitino.storage.relational.mapper.provider.h2.GovernanceMetadataH2SQLProvider;
import org.apache.gravitino.storage.relational.mapper.provider.postgresql.GovernanceMetadataPostgreSQLProvider;
import org.apache.gravitino.storage.relational.po.GovernanceMetadataPO;
import org.apache.gravitino.storage.relational.session.SqlSessionFactoryHelper;
import org.apache.ibatis.annotations.Param;

/** Selects database-specific governance metadata SQL. */
public class GovernanceMetadataSQLProviderFactory {
  private static final Map<JDBCBackend.JDBCBackendType, GovernanceMetadataBaseSQLProvider>
      PROVIDERS =
          ImmutableMap.of(
              JDBCBackend.JDBCBackendType.H2,
              new GovernanceMetadataH2SQLProvider(),
              JDBCBackend.JDBCBackendType.MYSQL,
              new GovernanceMetadataBaseSQLProvider(),
              JDBCBackend.JDBCBackendType.POSTGRESQL,
              new GovernanceMetadataPostgreSQLProvider());

  /** Returns select SQL. */
  public static String select(
      @Param("metalakeName") String metalakeName,
      @Param("objectType") String objectType,
      @Param("fullName") String fullName) {
    return provider().select(metalakeName, objectType, fullName);
  }

  /** Returns upsert SQL. */
  public static String upsert(@Param("metadata") GovernanceMetadataPO metadata) {
    return provider().upsert(metadata);
  }

  /** Returns delete SQL. */
  public static String delete(
      @Param("metalakeName") String metalakeName,
      @Param("objectType") String objectType,
      @Param("fullName") String fullName) {
    return provider().delete(metalakeName, objectType, fullName);
  }

  private static GovernanceMetadataBaseSQLProvider provider() {
    String databaseId =
        SqlSessionFactoryHelper.getInstance()
            .getSqlSessionFactory()
            .getConfiguration()
            .getDatabaseId();
    return PROVIDERS.get(JDBCBackend.JDBCBackendType.fromString(databaseId));
  }
}
