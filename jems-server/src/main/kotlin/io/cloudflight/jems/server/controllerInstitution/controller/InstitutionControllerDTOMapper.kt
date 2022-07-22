package io.cloudflight.jems.server.controllerInstitution.controller

import io.cloudflight.jems.api.controllerInstitutions.dto.ControllerInstitutionDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.ControllerInstitutionListDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.ControllerInstitutionUserDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.UpdateControllerInstitutionDTO
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import io.cloudflight.jems.server.nuts.service.groupNuts
import io.cloudflight.jems.server.nuts.service.toOutputNuts
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

private val mapper = Mappers.getMapper(ControllerInstitutionMapper::class.java)
private val userMapper = Mappers.getMapper(ControllerInstitutionUserMapper::class.java)

fun Page<ControllerInstitution>.toDto() = map { it.toDto() }
fun Page<ControllerInstitutionList>.toListDto() = map { it.toDto() }
fun ControllerInstitution.toDto() = mapper.map(this)
fun ControllerInstitutionList.toDto() = mapper.map(this)
fun ControllerInstitutionDTO.toModel() = mapper.map(this)
fun UpdateControllerInstitutionDTO.toModel() = mapper.mapUpdateDTO(this)

fun ControllerInstitutionUser.toDto() = userMapper.map(this)
fun ControllerInstitutionUserDTO.toModel() = userMapper.map(this)
fun Set<NutsRegion3>.toDto() = groupNuts(this).toOutputNuts()


@Mapper
interface ControllerInstitutionMapper {
    fun map(model: ControllerInstitution): ControllerInstitutionDTO
    fun map(model: ControllerInstitutionList): ControllerInstitutionListDTO
    fun map(dto: ControllerInstitutionDTO): ControllerInstitution
    fun mapUpdateDTO(dto: UpdateControllerInstitutionDTO): UpdateControllerInstitution
}

@Mapper
interface ControllerInstitutionUserMapper {
    fun map(model: ControllerInstitutionUser): ControllerInstitutionUserDTO
    fun map(dto: ControllerInstitutionUserDTO): ControllerInstitutionUser
}
