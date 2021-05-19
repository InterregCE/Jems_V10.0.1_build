package io.cloudflight.jems.server.factory

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.user.entity.UserEntity
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProjectPartnerFactory(
    val projectRepository: ProjectRepository,
    val legalStatusRepository: ProgrammeLegalStatusRepository,
    val projectPartnerRepository: ProjectPartnerRepository
) {

    @Transactional
    fun saveProjectPartner(author: UserEntity, project: ProjectEntity): ProjectPartnerEntity {
        val legalStatus = saveLegalStatus(author)

        return projectPartnerRepository.save(
            ProjectPartnerEntity(
                id = 0,
                project = project,
                abbreviation = "abbr",
                legalStatus = legalStatus,
                role = ProjectPartnerRole.PARTNER
            )
        )
    }

    @Transactional
    fun saveLegalStatus(author: UserEntity): ProgrammeLegalStatusEntity {
       return legalStatusRepository.save(
            ProgrammeLegalStatusEntity(
                id = 0,
                type = ProgrammeLegalStatusType.OTHER
            )
        )
    }
}
