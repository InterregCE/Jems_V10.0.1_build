package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.getControllerInstitution.GetControllerInstitutionException
import io.cloudflight.jems.server.controllerInstitution.service.model.*
import io.cloudflight.jems.server.nuts.repository.NutsRegion3Repository
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
class ControllerInstitutionPersistenceProvider(
    private val controllerRepo: ControllerInstitutionRepository,
    private val nutsRegion3Repository: NutsRegion3Repository,
    private val institutionUserRepository: ControllerInstitutionUserRepository,
    private val institutionPartnerRepository: ControllerInstitutionPartnerRepository,
    ): ControllerInstitutionPersistence {

    @Transactional(readOnly = true)
    override fun getControllerInstitutions(pageable: Pageable): Page<ControllerInstitutionList> =
        controllerRepo.findAll(pageable).toListModel()

    @Transactional(readOnly = true)
    override fun getControllerInstitutionsByUserId(userId: Long, pageable: Pageable): Page<ControllerInstitutionList> {
       val allowedControllerInstitutionIds = institutionUserRepository.findAllByUserId(userId).map { it.id.controllerInstitutionId }
       return controllerRepo.findAllByIdIn(allowedControllerInstitutionIds, pageable).toListModel()
    }

    @Transactional(readOnly = true)
    override fun getControllerInstitutionById(controllerInstitutionId: Long): ControllerInstitution {
        val getUsersAssignedToInstitution = institutionUserRepository.findAllByControllerInstitutionId(controllerInstitutionId)
        return this.getControllerInstitutionOrThrow(controllerInstitutionId).toModel(getUsersAssignedToInstitution)
    }


    @Transactional
    override fun createControllerInstitution(controllerInstitution: UpdateControllerInstitution): ControllerInstitution {
        val savedControllerInstitution = controllerRepo.save(controllerInstitution.toEntity())
        if (controllerInstitution.institutionNuts.isNotEmpty()) {
            savedControllerInstitution.institutionNuts = nutsRegion3Repository.findAllById(controllerInstitution.institutionNuts)
                .toMutableSet()
        }
        return savedControllerInstitution.toModel()
    }

    @Transactional
    override fun updateControllerInstitution(controllerInstitution: UpdateControllerInstitution): ControllerInstitution {
        val controllerInstitutionInstance = this.getControllerInstitutionOrThrow(controllerInstitution.id)

        controllerInstitutionInstance.description = controllerInstitution.description
        controllerInstitutionInstance.name = controllerInstitution.name
        controllerInstitutionInstance.institutionNuts = nutsRegion3Repository.findAllById(controllerInstitution.institutionNuts)
            .toMutableSet()
        return controllerInstitutionInstance.toModel()
    }

    @Transactional
    override fun updateControllerInstitutionUsers(
        institutionId: Long,
        userSummaries: List<UserSummary>,
        usersToUpdate: Set<ControllerInstitutionUser>,
        usersIdsToDelete: Set<Long>
    ) {
        if (usersIdsToDelete.isNotEmpty()) {
            institutionUserRepository.deleteAllByIdControllerInstitutionIdAndIdUserIdIn(institutionId, usersIdsToDelete)
        }

        institutionUserRepository.saveAll(usersToUpdate.map { userToSave ->
            userToSave.toEntity(
                institutionId,
                userSummaries.first { userToSave.userEmail == it.email })
        })
    }

    @Transactional(readOnly = true)
    override fun getInstitutionUserByInstitutionIdAndUserId(institutionId: Long, userId: Long): Optional<ControllerInstitutionUser> =
        institutionUserRepository.findByInstitutionIdAndUserId(institutionId = institutionId, userId = userId).map { it.toModel() }


    @Transactional(readOnly = true)
    override fun getInstitutionUsersByInstitutionId(institutionId: Long): List<ControllerInstitutionUser> {
       return institutionUserRepository.findAllByControllerInstitutionId(institutionId).map { it.toModel() }
    }

    @Transactional(readOnly = true)
    override fun getControllerInstitutionUsersByInstitutionIds(institutionIds: Set<Long>): List<ControllerInstitutionUser>  =
        institutionUserRepository.findAllByControllerInstitutionIdIn(institutionIds).map { it.toModel() }


    @Transactional(readOnly = true)
    override fun getInstitutionPartnerAssignments(pageable: Pageable): Page<InstitutionPartnerDetails> {
        return institutionPartnerRepository.getInstitutionPartnerAssignments(pageable, listOf(
            ApplicationStatus.STEP1_DRAFT,
            ApplicationStatus.STEP1_SUBMITTED,
            ApplicationStatus.STEP1_ELIGIBLE,
            ApplicationStatus.STEP1_INELIGIBLE,
            ApplicationStatus.STEP1_APPROVED,
            ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS,
            ApplicationStatus.STEP1_NOT_APPROVED,
            ApplicationStatus.DRAFT,
            ApplicationStatus.SUBMITTED,
            ApplicationStatus.CONDITIONS_SUBMITTED,
            ApplicationStatus.RETURNED_TO_APPLICANT,
            ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS,
            ApplicationStatus.ELIGIBLE,
            ApplicationStatus.INELIGIBLE,
            ApplicationStatus.APPROVED_WITH_CONDITIONS,
            ApplicationStatus.NOT_APPROVED,
        ))
    }

    @Transactional
    override fun assignInstitutionToPartner(
        assignmentsToRemove:  List<InstitutionPartnerAssignment>,
        assignmentsToSave: List<InstitutionPartnerAssignment>
    ): List<InstitutionPartnerAssignment> {
         if (assignmentsToRemove.isNotEmpty()) {
             institutionPartnerRepository.deleteAllByIdInBatch(assignmentsToRemove.map { it.partnerId })
         }
        return institutionPartnerRepository.saveAll(assignmentsToSave.toEntities()).toModels()
    }

    @Transactional
    override fun getInstitutionPartnerAssignmentsByPartnerIdsIn(partnerIds: Set<Long>): List<InstitutionPartnerAssignment> =
        institutionPartnerRepository.findAllByPartnerIdIn(partnerIds).toModels()

    @Transactional
    override fun getInstitutionPartnerAssignmentsByInstitutionId(institutionId: Long): List<InstitutionPartnerAssignment> =
        institutionPartnerRepository.findAllByInstitutionId(institutionId).toModels()


    @Transactional(readOnly = true)
    override fun getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(partnerProjectIds: Set<Long>): List<InstitutionPartnerAssignmentWithUsers> =
        institutionPartnerRepository.getInstitutionPartnerAssignmentsWithUsersByPartnerProjectIdsIn(partnerProjectIds)


    private fun getControllerInstitutionOrThrow(id: Long): ControllerInstitutionEntity =
        controllerRepo.findById(id).orElseThrow { GetControllerInstitutionException() }

}
