package io.cloudflight.jems.server.project.controller.partner

import io.cloudflight.jems.api.project.dto.ProjectContactDTO
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.ProjectPartnerStateAidDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDetailDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerContact
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerMotivation
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

fun ProjectPartnerStateAidDTO.toModel() = partnerDTOMapper.map(this)

fun ProjectPartnerStateAid.toDto() = partnerDTOMapper.map(this)

fun ProjectPartnerSummary.toDto() = partnerDTOMapper.map(this)
fun Page<ProjectPartnerSummary>.toDto(): Page<ProjectPartnerSummaryDTO> = map { it.toDto() }
fun List<ProjectPartnerSummary>.toDto() = map { it.toDto() }

fun ProjectPartnerDetail.toDto() = partnerDTOMapper.map(this)

fun ProjectPartnerDTO.toModel() = partnerDTOMapper.map(this)

fun Set<ProjectPartnerAddressDTO>.toAddressModel() = map { partnerDTOMapper.map(it)}.toSet()
fun ProjectPartnerMotivationDTO.toModel() = partnerDTOMapper.map(this)
fun Set<ProjectContactDTO>.toContactModel() = map { partnerDTOMapper.map(it)}.toSet()

private val partnerDTOMapper = Mappers.getMapper(ProjectPartnerDTOMapper::class.java)


@Mapper
abstract class ProjectPartnerDTOMapper {
    abstract fun map(projectPartnerStateAid: ProjectPartnerStateAid): ProjectPartnerStateAidDTO
    abstract fun map(projectPartnerStateAidDTO: ProjectPartnerStateAidDTO): ProjectPartnerStateAid
    abstract fun map(projectPartnerSummary: ProjectPartnerSummary): ProjectPartnerSummaryDTO
    abstract fun map(projectPartnerDetail: ProjectPartnerDetail): ProjectPartnerDetailDTO
    abstract fun map(projectPartnerDTO: ProjectPartnerDTO): ProjectPartner
    abstract fun map(projectPartnerAddressDTO: ProjectPartnerAddressDTO): ProjectPartnerAddress
    abstract fun map(projectContactDTO: ProjectContactDTO): ProjectPartnerContact

    fun map(projectPartnerMotivationDTO: ProjectPartnerMotivationDTO?): ProjectPartnerMotivation =
        ProjectPartnerMotivation(
            organizationRelevance = projectPartnerMotivationDTO?.organizationRelevance ?: emptySet(),
            organizationRole = projectPartnerMotivationDTO?.organizationRole ?: emptySet(),
            organizationExperience = projectPartnerMotivationDTO?.organizationExperience ?: emptySet()
        )

    fun map(projectPartnerMotivation: ProjectPartnerMotivation?): ProjectPartnerMotivationDTO =
        ProjectPartnerMotivationDTO(
            organizationRelevance = projectPartnerMotivation?.organizationRelevance ?: emptySet(),
            organizationRole = projectPartnerMotivation?.organizationRole ?: emptySet(),
            organizationExperience = projectPartnerMotivation?.organizationExperience ?: emptySet()
        )

}
