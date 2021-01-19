package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.server.project.entity.TranslationId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_description_c7_management_transl")
data class ProjectManagementTransl(

    @EmbeddedId
    val translationId: TranslationId,

    @Column
    val projectCoordination: String? = null,

    @Column
    val projectQualityAssurance: String? = null,

    @Column
    val projectCommunication: String? = null,

    @Column
    val projectFinancialManagement: String? = null,

    @Column
    val projectJointDevelopmentDescription: String? = null,

    @Column
    val projectJointImplementationDescription: String? = null,

    @Column
    val projectJointStaffingDescription: String? = null,

    @Column
    val projectJointFinancingDescription: String? = null,

    @Column
    val sustainableDevelopmentDescription: String? = null,

    @Column
    val equalOpportunitiesDescription: String? = null,

    @Column
    val sexualEqualityDescription: String? = null

)