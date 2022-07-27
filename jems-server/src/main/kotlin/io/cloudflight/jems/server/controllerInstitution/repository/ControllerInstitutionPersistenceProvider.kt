package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserEntity
import io.cloudflight.jems.server.controllerInstitution.service.controllerInstitutionChanged
import io.cloudflight.jems.server.controllerInstitution.service.getControllerInstitution.GetControllerInstitutionException
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.nuts.repository.NutsRegion3Repository
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
class ControllerInstitutionPersistenceProvider(
    private val controllerRepo: ControllerInstitutionRepository,
    private val nutsRegion3Repository: NutsRegion3Repository,
    private val userPersistence: UserPersistence,
    private val institutionUserRepository: ControllerInstitutionUserRepository,
    private val auditPublisher: ApplicationEventPublisher
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
        val newUsersSummaries = userPersistence.findAllByEmails(controllerInstitution.institutionUsers.map { it.userEmail })

        updateAssignedUsers(
            controllerInstitutionInstance = savedControllerInstitution,
            newInstitutionUsers = controllerInstitution.institutionUsers,
            newUsersSummaries = newUsersSummaries
        )

        if (controllerInstitution.institutionNuts.isNotEmpty()) {
            savedControllerInstitution.institutionNuts = nutsRegion3Repository.findAllById(controllerInstitution.institutionNuts)
                .toMutableSet()
        }

        return savedControllerInstitution.toModel(controllerInstitution.institutionUsers.toEntity(savedControllerInstitution, newUsersSummaries)).also {
            auditPublisher.publishEvent(
                controllerInstitutionChanged(
                    context = this,
                    controllerInstitution = it,
                    nutsRegion3 = controllerInstitution.institutionNuts
                )
            )
        }
    }

    @Transactional
    override fun updateControllerInstitution(controllerInstitution: UpdateControllerInstitution): ControllerInstitution {
        val controllerInstitutionInstance = this.getControllerInstitutionOrThrow(controllerInstitution.id)
        val newUsersSummaries = userPersistence.findAllByEmails(controllerInstitution.institutionUsers.map { it.userEmail })

        controllerInstitutionInstance.description = controllerInstitution.description
        controllerInstitutionInstance.name = controllerInstitution.name
        updateAssignedUsers(
            controllerInstitutionInstance = controllerInstitutionInstance,
            newInstitutionUsers =controllerInstitution.institutionUsers,
            newUsersSummaries = newUsersSummaries)

        controllerInstitutionInstance.institutionNuts = nutsRegion3Repository.findAllById(controllerInstitution.institutionNuts)
            .toMutableSet()

        return controllerInstitutionInstance.toModel(controllerInstitution.institutionUsers.toEntity(controllerInstitution.toEntity(), newUsersSummaries))
            .also {
                auditPublisher.publishEvent(
                    controllerInstitutionChanged(
                        context = this,
                        controllerInstitution = it,
                        nutsRegion3 = controllerInstitution.institutionNuts
                    )
                )
            }
    }

    @Transactional(readOnly = true)
    override fun getInstitutionUserByInstitutionIdAndUserId(institutionId: Long, userId: Long): Optional<ControllerInstitutionUser> =
        institutionUserRepository.findByInstitutionIdAndUserId(institutionId = institutionId, userId = userId).map { it.toModel() }


    private fun getControllerInstitutionOrThrow(id: Long): ControllerInstitutionEntity =
        controllerRepo.findById(id).orElseThrow { GetControllerInstitutionException() }


    private fun updateAssignedUsers(
        controllerInstitutionInstance: ControllerInstitutionEntity,
        newInstitutionUsers: List<ControllerInstitutionUser>,
        newUsersSummaries: List<UserSummary>
    ) {
        val newUsersIds = newUsersSummaries.map { it.id }
        val existingInstitutionUsers = institutionUserRepository.findAllByControllerInstitutionId(controllerInstitutionInstance.id)
        val usersIdsToBeDeleted = existingInstitutionUsers.map{it.id.user.id }.minus(newUsersIds.toSet())
        val usersToBeDeleted = existingInstitutionUsers.filter { it.id.user.id in usersIdsToBeDeleted }
        val usersToBeSaved: MutableList<ControllerInstitutionUserEntity> = mutableListOf()

        if (usersToBeDeleted.isNotEmpty()) {
            institutionUserRepository.deleteAll(usersToBeDeleted)
        }
        newInstitutionUsers.forEach { newUser ->
            val userToUpdate = existingInstitutionUsers.find { it.id.user.id == newUser.userId }
            if (userToUpdate != null) {
                userToUpdate.accessLevel = newUser.accessLevel
            } else {
                val userSummary = newUsersSummaries.find { it.email == newUser.userEmail }
                usersToBeSaved.add(newUser.toEntity(controllerInstitutionInstance, userSummary!!))
            }
            institutionUserRepository.saveAll(usersToBeSaved)
        }
    }

}
