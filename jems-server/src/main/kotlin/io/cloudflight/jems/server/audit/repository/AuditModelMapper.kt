package io.cloudflight.jems.server.audit.repository

import co.elastic.clients.elasticsearch.core.search.Hit
import co.elastic.clients.elasticsearch.core.search.HitsMetadata
import io.cloudflight.jems.server.audit.model.Audit
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

fun HitsMetadata<Audit>.toModel(pageable: Pageable) = PageImpl(
    hits().toModel(), pageable, total()?.value()!!
)

fun List<Hit<Audit>>.toModel(): List<Audit> =
    map {
        val audit = it.source() ?: Audit()
        audit.id = it.id()
        return@map audit
    }
