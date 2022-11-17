package io.cloudflight.jems.server.project.entity.contracting

import java.time.LocalDate
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_contracting_monitoring_add_date")
class ProjectContractingMonitoringAddDateEntity(

    @EmbeddedId
    val addDateId: ContractingMonitoringAddDateId,

    val entryIntoForceDate: LocalDate? = null,

    val comment: String? = null,

)
