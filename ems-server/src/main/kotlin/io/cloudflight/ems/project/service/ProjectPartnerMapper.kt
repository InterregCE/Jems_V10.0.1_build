package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.InputProjectPartnerContact
import io.cloudflight.ems.api.project.dto.InputProjectPartnerCreate
import io.cloudflight.ems.api.project.dto.OutputProjectPartner
import io.cloudflight.ems.api.project.dto.OutputProjectPartnerContact
import io.cloudflight.ems.api.project.dto.OutputProjectPartnerDetail
import io.cloudflight.ems.project.entity.Project
import io.cloudflight.ems.project.entity.ProjectPartner
import io.cloudflight.ems.project.entity.PartnerContactPerson
import io.cloudflight.ems.project.entity.PartnerContactPersonId

fun InputProjectPartnerCreate.toEntity(project: Project) = ProjectPartner(
    name = name!!,
    project = project,
    role = role!!
)

fun ProjectPartner.toOutputProjectPartner() = OutputProjectPartner(
    id = id,
    name = name,
    role = role,
    sortNumber = sortNumber
)

fun ProjectPartner.toOutputProjectPartnerDetail() = OutputProjectPartnerDetail(
    id = id,
    name = name,
    role = role,
    sortNumber = sortNumber,
    partnerContactPersons = partnerContactPersons?.map { it.toOutputProjectPartnerContact() }?.toHashSet()
)

fun InputProjectPartnerContact.toEntity(partner: ProjectPartner) = PartnerContactPerson(
    partnerContactPersonId = PartnerContactPersonId(partner.id!!, type),
    title = title,
    firstName =  firstName,
    lastName = lastName,
    email = email,
    telephone = telephone
)

fun PartnerContactPerson.toOutputProjectPartnerContact() = OutputProjectPartnerContact(
    type = partnerContactPersonId.type,
    title = title,
    firstName =  firstName,
    lastName = lastName,
    email = email,
    telephone = telephone
)
