package io.cloudflight.jems.server.project.entity.contracting

import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringOption
import java.time.LocalDate
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_contracting_monitoring")
class ProjectContractingMonitoringEntity(

    @Id
    @field:NotNull
    val projectId: Long,

    val startDate: LocalDate? = null,
    var closureDate: LocalDate?,

    @Enumerated(EnumType.STRING)
    @Column(name = "typology_prov_94")
    @field:NotNull
    val typologyProv94: ContractingMonitoringExtendedOption,
    @Column(name = "typology_prov_94_comment")
    val typologyProv94Comment: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "typology_prov_95")
    @field:NotNull
    val typologyProv95: ContractingMonitoringExtendedOption,
    @Column(name = "typology_prov_95_comment")
    val typologyProv95Comment: String? = null,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val typologyStrategic: ContractingMonitoringOption,
    val typologyStrategicComment: String? = null,
    @Enumerated(EnumType.STRING)
    @field:NotNull
    val typologyPartnership: ContractingMonitoringOption,
    val typologyPartnershipComment: String? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "addDateId.projectId")
    val addDates: List<ProjectContractingMonitoringAddDateEntity> = emptyList(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "projectId")
    val dimensionCodes: List<ContractingDimensionCodeEntity> = emptyList(),

)
