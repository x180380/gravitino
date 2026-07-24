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

import java.util.List;
import org.apache.gravitino.storage.relational.po.GovernanceMetadataPO;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/** Mapper for the isolated governance metadata extension table. */
public interface GovernanceMetadataMapper {
  String TABLE_NAME = "governance_metadata";

  @SelectProvider(type = GovernanceMetadataSQLProviderFactory.class, method = "select")
  GovernanceMetadataPO select(
      @Param("metalakeName") String metalakeName,
      @Param("objectType") String objectType,
      @Param("fullName") String fullName);

  @SelectProvider(type = GovernanceMetadataSQLProviderFactory.class, method = "list")
  List<GovernanceMetadataPO> list(
      @Param("metalakeName") String metalakeName, @Param("objectType") String objectType);

  @InsertProvider(type = GovernanceMetadataSQLProviderFactory.class, method = "upsert")
  void upsert(@Param("metadata") GovernanceMetadataPO metadata);

  @DeleteProvider(type = GovernanceMetadataSQLProviderFactory.class, method = "delete")
  int delete(
      @Param("metalakeName") String metalakeName,
      @Param("objectType") String objectType,
      @Param("fullName") String fullName);
}
