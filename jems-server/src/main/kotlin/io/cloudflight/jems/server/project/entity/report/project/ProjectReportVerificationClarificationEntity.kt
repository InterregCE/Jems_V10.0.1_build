package io.cloudflight.jems.server.project.entity.report.project

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_verification_clarification")
class ProjectReportVerificationClarificationEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @field:NotNull
    var number: Int,

    @ManyToOne
    @JoinColumn(name="project_report_id", referencedColumnName="id")
    @field:NotNull
    val projectReport: ProjectReportEntity,

    @field:NotNull
    var requestDate: LocalDate,

    var answerDate: LocalDate?,

    @field:NotNull
    var comment: String,
)
