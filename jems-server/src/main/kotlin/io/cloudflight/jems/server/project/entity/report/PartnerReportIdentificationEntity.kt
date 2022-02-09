package io.cloudflight.jems.server.project.entity.report

import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class PartnerReportIdentificationEntity(

    @field:NotNull
    val projectIdentifier: String,

    @field:NotNull
    val projectAcronym: String,

    @field:NotNull
    val partnerNumber: Int,

    @field:NotNull
    val partnerAbbreviation: String,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    val partnerRole: ProjectPartnerRole,

    val nameInOriginalLanguage: String?,

    val nameInEnglish: String?,

    @ManyToOne(optional = true)
    @JoinColumn(name = "legal_status_id")
    var legalStatus: ProgrammeLegalStatusEntity?,

    @Enumerated(EnumType.STRING)
    var partnerType: ProjectTargetGroup? = null,

    @Enumerated(EnumType.STRING)
    var vatRecovery: ProjectPartnerVatRecovery? = null,

) : Serializable {
    companion object {
        const val serialVersionUID = 1L
    }
}
