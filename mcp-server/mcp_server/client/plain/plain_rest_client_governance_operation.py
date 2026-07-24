# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.

from mcp_server.client.governance_operation import GovernanceOperation
from mcp_server.client.plain.utils import (
    encode_path_segment,
    extract_content_from_response,
    extract_response,
)


class PlainRESTClientGovernanceOperation(GovernanceOperation):
    """Business metadata operations backed by the Gravitino REST API."""

    def __init__(self, metalake_name: str, rest_client):
        self.metalake_name = metalake_name
        self.rest_client = rest_client

    def _object_path(self, object_type: str, full_name: str) -> str:
        return (
            f"/api/metalakes/{encode_path_segment(self.metalake_name)}"
            f"/governance/objects/{encode_path_segment(object_type)}"
            f"/{encode_path_segment(full_name)}"
        )

    async def get_business_metadata(
        self, object_type: str, full_name: str
    ) -> str:
        response = await self.rest_client.get(
            self._object_path(object_type, full_name)
        )
        return extract_content_from_response(response, "metadata", {})

    # pylint: disable=too-many-positional-arguments
    async def upsert_business_metadata(
        self,
        object_type: str,
        full_name: str,
        description: str | None,
        domain: str | None,
        tags: list[str],
        glossary_terms: list[str],
    ) -> str:
        response = await self.rest_client.put(
            self._object_path(object_type, full_name),
            json={
                "description": description,
                "domain": domain,
                "tags": tags,
                "glossaryTerms": glossary_terms,
            },
        )
        return extract_content_from_response(response, "metadata", {})

    async def delete_business_metadata(
        self, object_type: str, full_name: str
    ) -> str:
        response = await self.rest_client.delete(
            self._object_path(object_type, full_name)
        )
        return extract_response(response)
