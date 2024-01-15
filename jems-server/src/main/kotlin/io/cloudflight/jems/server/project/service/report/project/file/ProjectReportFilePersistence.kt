package io.cloudflight.jems.server.project.service.report.project.file

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata

interface ProjectReportFilePersistence {

    fun updateReportActivityAttachment(activityId: Long, file: JemsFileCreate): JemsFileMetadata

    fun updateReportDeliverableAttachment(deliverableId: Long, file: JemsFileCreate): JemsFileMetadata

    fun updateReportOutputAttachment(outputId: Long, file: JemsFileCreate): JemsFileMetadata

    fun updateProjectResultAttachment(reportId: Long, resultNumber: Int, file: JemsFileCreate): JemsFileMetadata

    fun addAttachmentToProjectReport(file: JemsFileCreate): JemsFile

    fun saveVerificationCertificateFile(file: JemsFileCreate): JemsFile

    fun saveAuditControlFile(file: JemsFileCreate): JemsFile

    fun countProjectReportVerificationCertificates(projectId: Long, reportId: Long): Long
}
