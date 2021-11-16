package io.cloudflight.jems.server.audit.repository

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.Audit
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.model.AuditUser
import org.elasticsearch.common.xcontent.XContentBuilder
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

const val FIELD_TIMESTAMP = "timestamp"
const val FIELD_ACTION = "action"
const val FIELD_USER = "user"
const val FIELD_USER_ID = "id"
const val FIELD_USER_EMAIL = "email"
const val FIELD_PROJECT = "project"
const val FIELD_PROJECT_ID = "id"
const val FIELD_PROJECT_CUSTOM_IDENTIFIER = "customIdentifier"
const val FIELD_PROJECT_NAME = "name"
const val FIELD_ENTITY_RELATED_ID = "entityRelatedId"
const val FIELD_DESCRIPTION = "description"

fun Audit.toElasticsearchEntity(): XContentBuilder {
    val entityBuilder = XContentFactory
        .jsonBuilder()
        .startObject()
        .timeField(FIELD_TIMESTAMP, getTimestampString())
        .field(FIELD_ACTION, action!!.name)
        .field(FIELD_ENTITY_RELATED_ID, entityRelatedId)
        .field(FIELD_DESCRIPTION, description)

    if (user != null) {
        entityBuilder.startObject(FIELD_USER)
            .field(FIELD_USER_ID, user.id)
            .field(FIELD_USER_EMAIL, user.email)
            .endObject()
    }

    if (project != null) {
        entityBuilder.startObject(FIELD_PROJECT)
            .field(FIELD_PROJECT_ID, project.id)
            .field(FIELD_PROJECT_CUSTOM_IDENTIFIER, project.customIdentifier)
            .field(FIELD_PROJECT_NAME, project.name)
            .endObject()
    }

    return entityBuilder.endObject()
}

private fun SearchHit.toModel(): Audit {
    var project: AuditProject? = null
    val projectSourceAsMap = sourceAsMap.getOrDefault(FIELD_PROJECT, null) as Map<*, *>?
    if (!projectSourceAsMap.isNullOrEmpty())
        project = AuditProject(
            id = projectSourceAsMap.getOrDefault(FIELD_PROJECT_ID, "") as String,
            customIdentifier = projectSourceAsMap.getOrDefault(FIELD_PROJECT_CUSTOM_IDENTIFIER, null) as String?,
            name = projectSourceAsMap.getOrDefault(FIELD_PROJECT_NAME, null) as String?
        )

    var user: AuditUser? = null
    val userSourceAsMap = sourceAsMap.getOrDefault(FIELD_USER, null) as Map<*, *>?
    if (!userSourceAsMap.isNullOrEmpty())
        user = AuditUser(
            id = (userSourceAsMap.getOrDefault(FIELD_USER_ID, 0L) as Number).toLong(),
            email = userSourceAsMap.getOrDefault(FIELD_USER_EMAIL, "") as String
        )

    return Audit(
        id = id,
        timestamp = Audit.getTimeFromString(sourceAsMap.getOrDefault(FIELD_TIMESTAMP, null) as String?),
        action = AuditAction.values().firstOrNull { it.name == sourceAsMap[FIELD_ACTION] },
        user = user,
        project = project,
        entityRelatedId = sourceAsMap.getOrDefault(FIELD_ENTITY_RELATED_ID, null) as Long?,
        description = sourceAsMap.getOrDefault(FIELD_DESCRIPTION, null) as String?,
    )
}

private fun Array<SearchHit>.toModel() = map { it.toModel() }

fun SearchHits.toModel(searchRequest: Pageable) = PageImpl(
    hits.toModel(), searchRequest, totalHits!!.value
)
