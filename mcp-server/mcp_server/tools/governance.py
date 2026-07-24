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

from fastmcp import Context, FastMCP


def load_governance_tools(mcp: FastMCP):
    """Register business governance metadata tools."""

    @mcp.tool(tags={"governance"})
    async def get_business_metadata(
        ctx: Context, object_type: str, full_name: str
    ) -> str:
        """Get business metadata attached to an object.

        Args:
            object_type: DATABASE, TABLE, or COLUMN.
            full_name: Object name in catalog.schema, catalog.schema.table,
                or catalog.schema.table.column format.
        """
        client = ctx.request_context.lifespan_context.rest_client()
        return await client.as_governance_operation().get_business_metadata(
            object_type, full_name
        )

    @mcp.tool(tags={"governance"})
    # pylint: disable=too-many-positional-arguments
    async def upsert_business_metadata(
        ctx: Context,
        object_type: str,
        full_name: str,
        description: str | None = None,
        domain: str | None = None,
        tags: list[str] | None = None,
        glossary_terms: list[str] | None = None,
    ) -> str:
        """Create or completely replace business metadata attached to an object.

        Args:
            object_type: DATABASE, TABLE, or COLUMN.
            full_name: Object name in catalog.schema, catalog.schema.table,
                or catalog.schema.table.column format.
            description: Business description, or null to clear it.
            domain: Business domain name, or null to clear it.
            tags: Complete replacement list of business tags.
            glossary_terms: Complete replacement list of glossary terms.
        """
        client = ctx.request_context.lifespan_context.rest_client()
        return await client.as_governance_operation().upsert_business_metadata(
            object_type,
            full_name,
            description,
            domain,
            tags or [],
            glossary_terms or [],
        )

    @mcp.tool(tags={"governance"})
    async def delete_business_metadata(
        ctx: Context, object_type: str, full_name: str
    ) -> str:
        """Delete business metadata attached to an object.

        Args:
            object_type: DATABASE, TABLE, or COLUMN.
            full_name: Fully qualified object name.
        """
        client = ctx.request_context.lifespan_context.rest_client()
        return await client.as_governance_operation().delete_business_metadata(
            object_type, full_name
        )
