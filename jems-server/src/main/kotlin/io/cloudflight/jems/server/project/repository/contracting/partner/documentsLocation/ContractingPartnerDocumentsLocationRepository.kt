package io.cloudflight.jems.server.project.repository.contracting.partner.documentsLocation

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerDocumentsLocationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ContractingPartnerDocumentsLocationRepository:
    JpaRepository<ProjectContractingPartnerDocumentsLocationEntity, Long> {

    fun findByProjectPartnerId(projectPartnerId: Long): Optional<ProjectContractingPartnerDocumentsLocationEntity>

}
