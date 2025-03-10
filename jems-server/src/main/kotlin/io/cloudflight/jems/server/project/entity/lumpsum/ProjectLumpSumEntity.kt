package io.cloudflight.jems.server.project.entity.lumpsum

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import java.time.ZonedDateTime
import javax.persistence.CascadeType
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_lump_sum")
data class ProjectLumpSumEntity(

    @EmbeddedId
    val id: ProjectLumpSumId,

    @ManyToOne(optional = false)
    @field:NotNull
    val programmeLumpSum: ProgrammeLumpSumEntity,

    val endPeriod: Int? = null,

    @field:NotNull
    var isReadyForPayment: Boolean = false,

    val comment: String? = null,

    @OneToMany(mappedBy = "id.projectLumpSumId", cascade = [CascadeType.ALL], orphanRemoval = true)
    val lumpSumContributions: Set<ProjectPartnerLumpSumEntity> = emptySet(),

    var paymentEnabledDate: ZonedDateTime?,

    var lastApprovedVersionBeforeReadyForPayment: String?
)
