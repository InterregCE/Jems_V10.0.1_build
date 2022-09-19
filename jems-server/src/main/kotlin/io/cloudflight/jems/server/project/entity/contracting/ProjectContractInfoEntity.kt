package io.cloudflight.jems.server.project.entity.contracting

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "project_contract_info")
class ProjectContractInfoEntity(

    @Id
    @field:NotNull
    val projectId: Long,

    val website: String?,

    val partnershipAgreementDate: LocalDate?,
)
