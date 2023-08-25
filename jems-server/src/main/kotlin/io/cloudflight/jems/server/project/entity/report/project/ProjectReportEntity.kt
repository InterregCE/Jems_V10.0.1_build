package io.cloudflight.jems.server.project.entity.report.project

import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTranslEntity
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.NamedAttributeNode
import javax.persistence.NamedEntityGraph
import javax.persistence.NamedEntityGraphs
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "report_project")
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "ProjectReportEntity.withTranslations",
        attributeNodes = [
            NamedAttributeNode(value = "translatedValues"),
        ],
    )
)
class ProjectReportEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @field:NotNull
    val projectId: Long,

    @field:NotNull
    var number: Int,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var status: ProjectReportStatus,

    @field:NotNull
    val applicationFormVersion: String,

    var startDate: LocalDate?,
    var endDate: LocalDate?,

    @ManyToOne
    var deadline: ProjectContractingReportingEntity?,
    @Enumerated(EnumType.STRING)
    var type: ContractingDeadlineType?,
    var periodNumber: Int?,
    var reportingDate: LocalDate?,

    @field:NotNull
    val projectIdentifier: String,
    @field:NotNull
    val projectAcronym: String,
    @field:NotNull
    val leadPartnerNameInOriginalLanguage: String,
    @field:NotNull
    val leadPartnerNameInEnglish: String,

    @field:NotNull
    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    var firstSubmission: ZonedDateTime?,
    var lastReSubmission: ZonedDateTime?,

    var verificationDate: LocalDate?,
    var verificationEndDate: ZonedDateTime?,
    var lastVerificationReOpening: ZonedDateTime?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<ProjectReportIdentificationTranslEntity> = mutableSetOf(),

    var verificationConclusionJs: String?,
    var verificationConclusionMa: String?,
    var verificationFollowup: String?,

    var riskBasedVerification: Boolean,
    var riskBasedVerificationDescription: String?
)
