package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.InputProjectPartnerContribution
import io.cloudflight.jems.api.project.dto.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.InputProjectPartnerOrganizationDetails
import io.cloudflight.jems.api.project.dto.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.OutputProjectPartnerDetail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectPartnerService {

    fun getById(projectId: Long, id: Long): OutputProjectPartnerDetail

    fun findAllByProjectId(projectId: Long, page: Pageable): Page<OutputProjectPartner>

    fun create(projectId: Long, projectPartner: InputProjectPartnerCreate): OutputProjectPartnerDetail

    fun update(projectId: Long, projectPartner: InputProjectPartnerUpdate): OutputProjectPartnerDetail

    fun updatePartnerContact(projectId: Long, partnerId: Long, projectPartnerContact: Set<InputProjectPartnerContact>): OutputProjectPartnerDetail

    fun updatePartnerContribution(projectId: Long, partnerId: Long, partnerContribution: InputProjectPartnerContribution): OutputProjectPartnerDetail

    fun updatePartnerOrganizationDetails(projectId: Long, partnerId: Long, partnerOrganizationDetails: Set<InputProjectPartnerOrganizationDetails>): OutputProjectPartnerDetail

    fun deletePartner(projectId: Long, partnerId: Long)
}
