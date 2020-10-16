package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.InputProjectPartnerContribution
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerAddress
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectPartnerService {

    fun getById(projectId: Long, id: Long): OutputProjectPartnerDetail

    fun findAllByProjectId(projectId: Long, page: Pageable): Page<OutputProjectPartner>

    fun create(projectId: Long, projectPartner: InputProjectPartnerCreate): OutputProjectPartnerDetail

    fun update(projectId: Long, projectPartner: InputProjectPartnerUpdate): OutputProjectPartnerDetail

    fun updatePartnerAddresses(projectId: Long, partnerId: Long, addresses: Set<InputProjectPartnerAddress>): OutputProjectPartnerDetail

    fun updatePartnerContacts(projectId: Long, partnerId: Long, contacts: Set<InputProjectPartnerContact>): OutputProjectPartnerDetail

    fun updatePartnerContribution(projectId: Long, partnerId: Long, partnerContribution: InputProjectPartnerContribution): OutputProjectPartnerDetail

    fun deletePartner(projectId: Long, partnerId: Long)
}
