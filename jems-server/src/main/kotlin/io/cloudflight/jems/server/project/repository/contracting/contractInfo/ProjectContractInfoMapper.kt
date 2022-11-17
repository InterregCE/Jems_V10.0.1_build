package io.cloudflight.jems.server.project.repository.contracting.contractInfo

import io.cloudflight.jems.server.project.entity.contracting.ProjectContractInfoEntity
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers

private val projectContractInfoMapper = Mappers.getMapper(ProjectContractInfoMapper::class.java)

fun ProjectContractInfoEntity.toModel() = projectContractInfoMapper.map(this)

fun ProjectContractInfo.toEntity(projectId: Long) =
    projectContractInfoMapper.contractInfoToEntity(projectId, this)

@Mapper
abstract class ProjectContractInfoMapper {

    @Mappings(
        Mapping(target = "projectStartDate", ignore = true),
        Mapping(target = "projectEndDate", ignore = true),
        Mapping(target = "subsidyContractDate", ignore = true),
    )
   abstract fun map(entity: ProjectContractInfoEntity): ProjectContractInfo


    fun contractInfoToEntity(projectId: Long, model: ProjectContractInfo): ProjectContractInfoEntity =
        ProjectContractInfoEntity(
            projectId = projectId,
            website = model.website,
            partnershipAgreementDate = model.partnershipAgreementDate
        )

}
