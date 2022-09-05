package io.cloudflight.jems.server.controllerInstitution.controller

import io.cloudflight.jems.api.controllerInstitutions.dto.ControllerInstitutionAssignmentDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.InstitutionPartnerAssignmentDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.InstitutionPartnerDetailsDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.UserInstitutionAccessLevelDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.common.toIdNamePairDTO
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.project.service.partner.getPartnerAddressOrEmptyString
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page


private val mapper = Mappers.getMapper(InstitutionPartnerDTOMapper::class.java)

fun Page<InstitutionPartnerDetails>.toPageDto() = map { it.toDto() }

fun ControllerInstitutionAssignmentDTO.toModel() = mapper.map(this)

fun List<InstitutionPartnerAssignment>.toDTOs() = map { it.toDto() }

fun InstitutionPartnerAssignment.toDto() = mapper.map(this)

fun InstitutionPartnerAssignmentDTO.toModel() = mapper.map(this)

fun InstitutionPartnerDetails.toDto() = InstitutionPartnerDetailsDTO(
    institutionId = institutionId,
    partnerId = partnerId,
    partnerName = partnerName,
    partnerStatus = partnerStatus,
    partnerRole = ProjectPartnerRoleDTO.valueOf(partnerRole.name),
    partnerSortNumber = partnerSortNumber,
    partnerNuts3 = partnerNuts3,
    partnerAddress = getPartnerAddressOrEmptyString(country, city, postalCode),
    callId = callId,
    projectId = projectId,
    projectCustomIdentifier = projectCustomIdentifier,
    projectAcronym = projectAcronym,
    partnerNutsCompatibleInstitutions = partnerNutsCompatibleInstitutions?.map { it.toIdNamePairDTO() }?.toSet()
)

fun UserInstitutionAccessLevel?.toDto() = mapper.map(this)

@Mapper
interface InstitutionPartnerDTOMapper {

    fun map(dto: ControllerInstitutionAssignmentDTO): ControllerInstitutionAssignment
    fun map(dto: ControllerInstitutionAssignment): ControllerInstitutionAssignmentDTO
    @Mapping(target = "partnerProjectId", ignore = true)
    fun map(dto: InstitutionPartnerAssignmentDTO): InstitutionPartnerAssignment

    fun map(model: InstitutionPartnerAssignment): InstitutionPartnerAssignmentDTO

    fun map(model: InstitutionPartnerDetails): InstitutionPartnerDetailsDTO
    fun map(model: UserInstitutionAccessLevel?): UserInstitutionAccessLevelDTO
}
