package io.cloudflight.jems.server.project.entity.contracting.reporting

import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_contracting_reporting")
class ProjectContractingReportingEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @field:NotNull
    val project: ProjectEntity,

    @field:NotNull
    @Enumerated(EnumType.STRING)
    var type: ContractingDeadlineType,

    @field:NotNull
    var periodNumber: Int,

    @field:NotNull
    var deadline: LocalDate,

    @field:NotNull
    var comment: String,

)
