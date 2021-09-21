package io.cloudflight.jems.server.project.repository.description

import io.cloudflight.jems.server.project.entity.description.ProjectManagementEntity
import io.cloudflight.jems.server.project.entity.description.ProjectManagementRow
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ProjectManagementRepository : PagingAndSortingRepository<ProjectManagementEntity, Long> {

    fun findFirstByProjectId(projectId: Long): ProjectManagementEntity?

    @Query(
        """
            SELECT
             entity.project_id AS projectId,
             entity.project_joint_development AS projectJointDevelopment,
             entity.project_joint_implementation AS projectJointImplementation,
             entity.project_joint_staffing AS projectJointStaffing,
             entity.project_joint_financing AS projectJointFinancing,
             entity.sustainable_development_criteria_effect AS sustainableDevelopmentCriteriaEffect,
             entity.equal_opportunities_effect AS equalOpportunitiesEffect,
             entity.sexual_equality_effect AS sexualEqualityEffect,
             translation.language AS language,
             translation.project_coordination AS projectCoordination,
             translation.project_quality_assurance AS projectQualityAssurance,
             translation.project_communication AS projectCommunication,
             translation.project_financial_management AS projectFinancialManagement,
             translation.project_joint_development_description AS projectJointDevelopmentDescription,
             translation.project_joint_implementation_description AS projectJointImplementationDescription,
             translation.project_joint_staffing_description AS projectJointStaffingDescription,
             translation.project_joint_financing_description AS projectJointFinancingDescription,
             translation.sustainable_development_description AS sustainableDevelopmentDescription,
             translation.equal_opportunities_description AS equalOpportunitiesDescription,
             translation.sexual_equality_description AS sexualEqualityDescription
             FROM #{#entityName} FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS entity
             LEFT JOIN #{#entityName}_transl FOR SYSTEM_TIME AS OF TIMESTAMP :timestamp AS translation ON entity.project_id = translation.project_id
             WHERE entity.project_id = :projectId
             ORDER BY entity.project_id
             """,
        nativeQuery = true
    )
    fun findByProjectIdAsOfTimestamp(projectId: Long, timestamp: Timestamp): List<ProjectManagementRow>

}
