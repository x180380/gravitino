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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.gravitino.dto.governance.GovernanceMetadataDTO;
import org.apache.gravitino.dto.requests.GovernanceMetadataUpdateRequest;
import org.apache.gravitino.json.JsonUtils;
import org.apache.gravitino.storage.relational.mapper.GovernanceMetadataMapper;
import org.apache.gravitino.storage.relational.po.GovernanceMetadataPO;
import org.apache.gravitino.storage.relational.utils.SessionUtils;

/**
 * Manages business governance metadata independently from catalog-native physical metadata.
 *
 * <p>The extension intentionally addresses objects by stable REST coordinates instead of modifying
 * Gravitino entity tables. This keeps upgrades from the upstream project localized.
 */
public class GovernanceMetadataManager {
  private static final Set<String> SUPPORTED_TYPES = Set.of("DATABASE", "TABLE", "COLUMN");
  private static final TypeReference<List<String>> STRING_LIST =
      new TypeReference<List<String>>() {};

  /** Returns governance metadata for an object. */
  public Optional<GovernanceMetadataDTO> get(
      String metalakeName, String objectType, String fullName) {
    String normalizedType = normalizeAndValidate(metalakeName, objectType, fullName);
    GovernanceMetadataPO metadata =
        SessionUtils.getWithoutCommit(
            GovernanceMetadataMapper.class,
            mapper -> mapper.select(metalakeName, normalizedType, fullName));
    return Optional.ofNullable(metadata).map(this::toDTO);
  }

  /** Creates or completely replaces governance metadata for an object. */
  public GovernanceMetadataDTO upsert(
      String metalakeName,
      String objectType,
      String fullName,
      GovernanceMetadataUpdateRequest request) {
    String normalizedType = normalizeAndValidate(metalakeName, objectType, fullName);
    request.validate();

    GovernanceMetadataPO metadata = new GovernanceMetadataPO();
    metadata.setMetalakeName(metalakeName);
    metadata.setObjectType(normalizedType);
    metadata.setFullName(fullName);
    metadata.setDescription(request.getDescription());
    metadata.setDomainName(request.getDomain());
    metadata.setTags(writeList(request.getTags()));
    metadata.setGlossaryTerms(writeList(request.getGlossaryTerms()));
    metadata.setUpdatedAt(System.currentTimeMillis());
    SessionUtils.doWithCommit(GovernanceMetadataMapper.class, mapper -> mapper.upsert(metadata));
    return toDTO(metadata);
  }

  /** Deletes governance metadata for an object. */
  public boolean delete(String metalakeName, String objectType, String fullName) {
    String normalizedType = normalizeAndValidate(metalakeName, objectType, fullName);
    return SessionUtils.doWithCommitAndFetchResult(
            GovernanceMetadataMapper.class,
            mapper -> mapper.delete(metalakeName, normalizedType, fullName))
        > 0;
  }

  private static String normalizeAndValidate(
      String metalakeName, String objectType, String fullName) {
    if (StringUtils.isAnyBlank(metalakeName, objectType, fullName)) {
      throw new IllegalArgumentException("metalake, objectType, and fullName must not be empty");
    }
    String normalizedType = objectType.toUpperCase(Locale.ROOT);
    if (!SUPPORTED_TYPES.contains(normalizedType)) {
      throw new IllegalArgumentException(
          "Unsupported object type: "
              + objectType
              + ". Supported types are DATABASE, TABLE, and COLUMN");
    }
    return normalizedType;
  }

  private GovernanceMetadataDTO toDTO(GovernanceMetadataPO metadata) {
    return new GovernanceMetadataDTO(
        metadata.getObjectType(),
        metadata.getFullName(),
        metadata.getDescription(),
        metadata.getDomainName(),
        readList(metadata.getTags()),
        readList(metadata.getGlossaryTerms()),
        metadata.getUpdatedAt());
  }

  private static String writeList(List<String> value) {
    try {
      return JsonUtils.anyFieldMapper().writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Failed to serialize governance metadata", e);
    }
  }

  private static List<String> readList(String value) {
    if (value == null) {
      return Collections.emptyList();
    }
    try {
      return JsonUtils.anyFieldMapper().readValue(value, STRING_LIST);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Failed to deserialize governance metadata", e);
    }
  }
}
