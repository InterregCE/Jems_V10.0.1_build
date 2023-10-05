package io.cloudflight.jems.server.payments.entity

import com.querydsl.core.annotations.QueryInit
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.lumpsum.ProjectLumpSumEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "payment")
class PaymentEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val type: PaymentType,

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    @field:NotNull
    @QueryInit("*.*.*", "priorityPolicy.programmePriority.code") // to support deeper QueryDSL joins
    val project: ProjectEntity,

    @field:NotNull
    val projectCustomIdentifier: String,
    @field:NotNull
    val projectAcronym: String,

    @ManyToOne(optional = true)
    // join 2 columns
    @JoinColumns(
        JoinColumn(name = "project_lump_sum_id", referencedColumnName = "project_id"),
        JoinColumn(name = "order_nr", referencedColumnName = "order_nr")
    )
    val projectLumpSum: ProjectLumpSumEntity?,

    @ManyToOne(optional = true)
    @JoinColumn(name = "project_report_id", referencedColumnName = "id")
    val projectReport: ProjectReportEntity?,

    @ManyToOne
    @JoinColumn(name = "programme_fund_id")
    @field:NotNull
    val fund: ProgrammeFundEntity,

    val amountApprovedPerFund: BigDecimal?,

)
