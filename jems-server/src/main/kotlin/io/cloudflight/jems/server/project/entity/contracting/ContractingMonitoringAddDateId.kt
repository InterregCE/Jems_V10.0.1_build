package io.cloudflight.jems.server.project.entity.contracting

import java.io.Serializable
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotNull

@Embeddable
class ContractingMonitoringAddDateId(

    @Column(name = "project_id")
    @field:NotNull
    val projectId: Long,

    @field:NotNull
    val number: Int

): Serializable {
    override fun equals(other: Any?): Boolean = this === other ||
    other is ContractingMonitoringAddDateId
    && projectId == other.projectId
    && number == other.number

    override fun hashCode() = Objects.hash(projectId, number)
}
