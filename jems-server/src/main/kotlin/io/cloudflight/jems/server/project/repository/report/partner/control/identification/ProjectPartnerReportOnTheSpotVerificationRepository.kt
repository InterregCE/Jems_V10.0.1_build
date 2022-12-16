package io.cloudflight.jems.server.project.repository.report.partner.control.identification

import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportOnTheSpotVerificationEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ProjectPartnerReportOnTheSpotVerificationRepository :
    JpaRepository<ProjectPartnerReportOnTheSpotVerificationEntity, Long> {
    override fun findById(id: Long): Optional<ProjectPartnerReportOnTheSpotVerificationEntity>
    }
