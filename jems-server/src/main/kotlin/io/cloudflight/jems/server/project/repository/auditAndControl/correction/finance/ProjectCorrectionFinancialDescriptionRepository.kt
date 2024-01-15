package io.cloudflight.jems.server.project.repository.auditAndControl.correction.finance

import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionFinanceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectCorrectionFinancialDescriptionRepository: JpaRepository<AuditControlCorrectionFinanceEntity, Long>
