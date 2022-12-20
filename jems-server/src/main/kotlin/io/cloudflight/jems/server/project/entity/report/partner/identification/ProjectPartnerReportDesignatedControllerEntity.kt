package io.cloudflight.jems.server.project.entity.report.partner.identification

import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapsId
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_designated_controller")
data class ProjectPartnerReportDesignatedControllerEntity (
    @Id
    val reportId: Long = 0,

    @OneToOne
    @JoinColumn(name = "report_id")
    @MapsId
    @field:NotNull
    val reportEntity: ProjectPartnerReportEntity,

    @ManyToOne
    @JoinColumn(name = "institution_id")
    @field:NotNull
    val controlInstitution: ControllerInstitutionEntity,

    @ManyToOne(optional = true)
    @JoinColumn(name = "control_user_id")
    var controllingUser: UserEntity?,

    @ManyToOne(optional = true)
    @JoinColumn(name = "review_user_id")
    var controllerReviewer: UserEntity?,

    var institutionName: String?,
    var jobTitle: String?,
    var divisionUnit: String?,
    var address: String?,
    var countryCode: String?,
    var country: String?,
    var telephone: String?,
)
