package io.cloudflight.jems.server.project.service.report.project.file

import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface ProjectReportFilePersistence {

    fun updateReportActivityAttachment(activityId: Long, file: JemsFileCreate): JemsFileMetadata

    fun updateReportDeliverableAttachment(deliverableId: Long, file: JemsFileCreate): JemsFileMetadata

    fun updateReportOutputAttachment(outputId: Long, file: JemsFileCreate): JemsFileMetadata

    fun updateProjectResultAttachment(reportId: Long, resultNumber: Int, file: JemsFileCreate): JemsFileMetadata

}
