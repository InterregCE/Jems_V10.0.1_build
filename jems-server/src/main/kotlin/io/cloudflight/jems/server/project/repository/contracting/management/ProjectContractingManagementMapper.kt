package io.cloudflight.jems.server.project.repository.contracting.management

import io.cloudflight.jems.server.project.entity.Contact
import io.cloudflight.jems.server.project.entity.contracting.ContractingManagementId
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingManagementEntity
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers

private val contractingManagementMapper = Mappers.getMapper(ProjectContractingManagementMapper::class.java)


fun List<ProjectContractingManagementEntity>.toModelList() = map { contractingManagementMapper.map(it)}

fun List<ProjectContractingManagement>.toEntities() = map { it.toEntity() }


fun ProjectContractingManagement.toEntity() = ProjectContractingManagementEntity(
    managementId = ContractingManagementId(projectId, managementType),
    contact = Contact(
        title = title,
        firstName = firstName,
        lastName = lastName,
        email = email,
        telephone = telephone
    )
)

@Mapper
interface ProjectContractingManagementMapper {

    @Mappings(
        Mapping(source = "managementId.projectId", target = "projectId"),
        Mapping(source = "managementId.managementType", target = "managementType"),
        Mapping(source = "contact.title", target = "title"),
        Mapping(source = "contact.firstName", target = "firstName"),
        Mapping(source = "contact.lastName", target = "lastName"),
        Mapping(source = "contact.email", target = "email"),
        Mapping(source = "contact.telephone", target = "telephone")
    )
    fun map(entity: ProjectContractingManagementEntity): ProjectContractingManagement
}
