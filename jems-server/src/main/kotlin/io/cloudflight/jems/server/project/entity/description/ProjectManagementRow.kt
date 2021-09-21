package io.cloudflight.jems.server.project.entity.description

import io.cloudflight.jems.api.project.dto.description.ProjectHorizontalPrinciplesEffect
import io.cloudflight.jems.server.common.entity.TranslationView

interface ProjectManagementRow: TranslationView {
    val projectId: Long

    // ProjectCooperationCriteria
    val projectJointDevelopment: Boolean?
    val projectJointImplementation: Boolean?
    val projectJointStaffing: Boolean?
    val projectJointFinancing: Boolean?

    // ProjectHorizontalPrinciples
    val sustainableDevelopmentCriteriaEffect: ProjectHorizontalPrinciplesEffect?
    val equalOpportunitiesEffect: ProjectHorizontalPrinciplesEffect?
    val sexualEqualityEffect: ProjectHorizontalPrinciplesEffect?

    // _long_term_plans_transl
    val projectCoordination: String?
    val projectQualityAssurance: String?
    val projectCommunication: String?
    val projectFinancialManagement: String?
    val projectJointDevelopmentDescription: String?
    val projectJointImplementationDescription: String?
    val projectJointStaffingDescription: String?
    val projectJointFinancingDescription: String?
    val sustainableDevelopmentDescription: String?
    val equalOpportunitiesDescription: String?
    val sexualEqualityDescription: String?
}
