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

from abc import ABC, abstractmethod


class GovernanceOperation(ABC):
    """Operations for independently stored business governance metadata."""

    @abstractmethod
    async def get_business_metadata(
        self, object_type: str, full_name: str
    ) -> str:
        """Get business metadata for a database, table, or column."""
        pass

    @abstractmethod
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
        """Create or completely replace business metadata for an object."""
        pass

    @abstractmethod
    async def delete_business_metadata(
        self, object_type: str, full_name: str
    ) -> str:
        """Delete business metadata for an object."""
        pass
