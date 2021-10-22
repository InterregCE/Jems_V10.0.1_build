package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.user.repository.user.UserRepository
import java.time.LocalDate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectWorkflowPersistenceProvider(
    private val projectStatusHistoryRepository: ProjectStatusHistoryRepository,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
) : ProjectWorkflowPersistence {

    @Transactional(readOnly = true)
    override fun getProjectEligibilityDecisionDate(projectId: Long): LocalDate? =
        getProjectOrThrow(projectId)!!.let {
            if (it.currentStatus.status.isInStep2())
                it.decisionEligibilityStep2?.decisionDate
            else
                it.decisionEligibilityStep1?.decisionDate
        }

    @Transactional(readOnly = true)
    override fun getApplicationPreviousStatus(projectId: Long): ProjectStatus =
        getPreviousHistoryStatusOrThrow(projectId).toProjectStatus()

    @Transactional(readOnly = true)
    override fun getLatestApplicationStatusNotEqualTo(
        projectId: Long, statusToIgnore: ApplicationStatus
    ): ApplicationStatus =
        projectStatusHistoryRepository.findFirstByProjectIdAndStatusNotOrderByUpdatedDesc(
            projectId, statusToIgnore
        )?.status ?: throw ApplicationStatusNotFoundException()

    @Transactional
    override fun updateApplicationFirstSubmission(projectId: Long, userId: Long, status: ApplicationStatus) =
        projectRepository.getOne(projectId).apply {
            val newStatus = projectStatusHistoryRepository.save(
                ProjectStatusHistoryEntity(
                    project = this, status = status, user = userRepository.getOne(userId)
                )
            )
            firstSubmission = newStatus
            currentStatus = newStatus
        }.currentStatus.status

    @Transactional
    override fun updateProjectLastResubmission(projectId: Long, userId: Long, status: ProjectStatus) =
        updateProjectLastResubmission(projectId, userId, status.status, status.decisionDate, status.note)

    @Transactional
    override fun updateProjectLastResubmission(projectId: Long, userId: Long, status: ApplicationStatus) =
        updateProjectLastResubmission(projectId, userId, status, null, null)

    private fun updateProjectLastResubmission(
        projectId: Long,
        userId: Long,
        status: ApplicationStatus,
        decisionDate: LocalDate? = null,
        note: String? = null
    ) =
        projectRepository.getOne(projectId).apply {
            val newStatus = projectStatusHistoryRepository.save(
                ProjectStatusHistoryEntity(
                    project = this,
                    status = status,
                    user = userRepository.getOne(userId),
                    decisionDate = decisionDate,
                    note = note,
                )
            )
            lastResubmission = newStatus
            currentStatus = newStatus
        }.currentStatus.status

    @Transactional
    override fun updateProjectCurrentStatus(
        projectId: Long,
        userId: Long,
        status: ApplicationStatus,
        actionInfo: ApplicationActionInfo?
    ) =
        projectRepository.getOne(projectId).apply {
            currentStatus = projectStatusHistoryRepository.save(
                ProjectStatusHistoryEntity(
                    project = this, status = status, user = userRepository.getOne(userId),
                    decisionDate = actionInfo?.date,
                    note = actionInfo?.note
                )
            )
        }.currentStatus.status

    @Transactional
    override fun startSecondStep(
        projectId: Long,
        userId: Long,
        actionInfo: ApplicationActionInfo?
    ): ApplicationStatus =
        projectRepository.getOne(projectId).apply {
            currentStatus = projectStatusHistoryRepository.save(
                ProjectStatusHistoryEntity(
                    project = this, status = ApplicationStatus.DRAFT, user = userRepository.getOne(userId),
                    decisionDate = actionInfo?.date,
                    note = actionInfo?.note
                )
            )
        }.currentStatus.status

    @Transactional
    override fun revertCurrentStatusToPreviousStatus(projectId: Long) =
        getPreviousHistoryStatusOrThrow(projectId).let { previousHistoryStatus ->
            projectRepository.getOne(projectId).apply {
                projectStatusHistoryRepository.delete(currentStatus)
                currentStatus = previousHistoryStatus
            }
        }.currentStatus.status

    @Transactional
    override fun resetProjectFundingDecisionToCurrentStatus(projectId: Long) =
        projectRepository.getOne(projectId).apply {
            if (this.currentStatus.status.isInStep2())
                if (this.currentStatus.status == ApplicationStatus.APPROVED_WITH_CONDITIONS)
                    this.decisionFundingStep2 = null
                else
                    this.decisionPreFundingStep2 = null
            else
                this.decisionFundingStep1 = null
        }.currentStatus.status


    @Transactional
    override fun updateProjectEligibilityDecision(
        projectId: Long, userId: Long, status: ApplicationStatus, actionInfo: ApplicationActionInfo
    ) =
        projectRepository.getOne(projectId).apply {
            val newStatus = projectStatusHistoryRepository.save(
                ProjectStatusHistoryEntity(
                    project = this, status = status, note = actionInfo.note,
                    decisionDate = actionInfo.date, user = userRepository.getOne(userId)
                )
            )
            if (this.currentStatus.status.isInStep2())
                this.decisionEligibilityStep2 = newStatus
            else
                this.decisionEligibilityStep1 = newStatus
            currentStatus = newStatus
        }.currentStatus.status

    @Transactional
    override fun clearProjectEligibilityDecision(projectId: Long) {
        projectRepository.getOne(projectId).apply {
            if (this.currentStatus.status.isInStep2())
                this.decisionEligibilityStep2 = null
            else
                this.decisionEligibilityStep1 = null
        }
    }

    @Transactional
    override fun updateProjectFundingDecision(
        projectId: Long, userId: Long, status: ApplicationStatus, actionInfo: ApplicationActionInfo
    ) =
        projectRepository.getOne(projectId).apply {
            val newStatus = projectStatusHistoryRepository.save(
                ProjectStatusHistoryEntity(
                    project = this, status = status, note = actionInfo.note,
                    decisionDate = actionInfo.date, user = userRepository.getOne(userId)
                )
            )
            if (this.currentStatus.status.isInStep2())
                if (status == ApplicationStatus.APPROVED_WITH_CONDITIONS)
                    this.decisionPreFundingStep2 = newStatus
                else
                    this.decisionFundingStep2 = newStatus
            else
                this.decisionFundingStep1 = newStatus
            currentStatus = newStatus
        }.currentStatus.status

    @Transactional
    override fun clearProjectFundingDecision(projectId: Long) {
        projectRepository.getOne(projectId).apply {
            if (this.currentStatus.status.isInStep2())
                if (this.currentStatus.status == ApplicationStatus.APPROVED_WITH_CONDITIONS)
                    this.decisionPreFundingStep2 = null
                else
                    this.decisionFundingStep2 = null
            else
                this.decisionFundingStep1 = null
        }
    }

    private fun getProjectOrThrow(projectId: Long) =
        projectRepository.findById(projectId).orElseThrow { ResourceNotFoundException("project") }

    private fun getPreviousHistoryStatusOrThrow(projectId: Long): ProjectStatusHistoryEntity =
        projectStatusHistoryRepository.findTop2ByProjectIdOrderByUpdatedDesc(projectId)
            .run {
                if (size != 2)
                    throw PreviousApplicationStatusNotFoundException()
                last()
            }
}
