package io.cloudflight.jems.server.audit.entity

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Document(indexName = "audit-log")
data class Audit(
    @Id
    @Field(type = FieldType.Text)
    val id: String? = null,

    @Field(type = FieldType.Date, store = true, format = DateFormat.date_time)
    val timestamp: String? = getElasticTimeNow(),

    @Field(type = FieldType.Keyword, store = true)
    val action: AuditAction?,

    @Field(type = FieldType.Keyword, store = true)
    val projectId: String? = null,

    @Field(type = FieldType.Object, store = true)
    val user: AuditUser?,

    @Field(type = FieldType.Text, store = true, index = false)
    val description: String?
) {

    companion object {

        private fun getElasticTimeNow(): String {
            return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME)
        }

    }

    // default constructor is needed for deserialization
    constructor() : this(null, null, null, null, null, null)

}
