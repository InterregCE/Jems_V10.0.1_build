package io.cloudflight.jems.server.project.repository.report.file

import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportFileRepository : JpaRepository<ReportProjectFileEntity, Long> {

    fun existsByPartnerIdAndId(partnerId: Long, fileId: Long): Boolean

    fun findByPartnerIdAndId(partnerId: Long, fileId: Long): ReportProjectFileEntity?

}
