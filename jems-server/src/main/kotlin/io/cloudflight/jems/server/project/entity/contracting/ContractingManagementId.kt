package io.cloudflight.jems.server.project.entity.contracting

import io.cloudflight.jems.server.project.service.contracting.model.ManagementType
import java.io.Serializable
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotNull

@Embeddable
class ContractingManagementId(
    @Column(name = "project_id")
    val projectId: Long,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val managementType: ManagementType
): Serializable {
    override fun equals(other: Any?): Boolean = this === other ||
    other is ContractingManagementId
    && projectId == other.projectId
    && managementType == other.managementType

    override fun hashCode() = Objects.hash(projectId, managementType)
}
