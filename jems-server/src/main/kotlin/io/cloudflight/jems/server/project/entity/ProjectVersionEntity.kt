package io.cloudflight.jems.server.project.entity

import io.cloudflight.jems.server.project.repository.workpackage.TableRelation
import io.cloudflight.jems.server.user.entity.UserEntity
import java.sql.Timestamp
import java.time.ZonedDateTime
import javax.persistence.ColumnResult
import javax.persistence.ConstructorResult
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SqlResultSetMapping
import javax.validation.constraints.NotNull

@SqlResultSetMapping(
    name = "tableRelationMapping",
    classes = [
        ConstructorResult(
            columns = [
                ColumnResult(name = "childTable"),
                ColumnResult(name = "childColumn"),
                ColumnResult(name = "parentTable"),
                ColumnResult(name = "parentColumn")
            ],
            targetClass = TableRelation::class
        )
    ]
)

@Entity(name = "project_version")
class ProjectVersionEntity(

    @EmbeddedId
    val id: ProjectVersionId,

    @Transient
    val rowEnd: Timestamp? = null,

    @field:NotNull
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @field:NotNull
    val user: UserEntity,

    )
