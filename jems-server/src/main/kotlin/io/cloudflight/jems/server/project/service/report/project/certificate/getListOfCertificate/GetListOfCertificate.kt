package io.cloudflight.jems.server.project.service.report.project.certificate.getListOfCertificate

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.JpaSort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetListOfCertificate(
    private val partnerPersistence: PartnerPersistence,
    private val projectReportCertificatePersistence: ProjectReportCertificatePersistence,
) : GetListOfCertificateInteractor {

    companion object {
        private fun defaultSort(id: Long): Sort = JpaSort.unsafe(Sort.Direction.DESC,
            "project_report_id IS NULL","project_report_id = $id", "control_end", "id"
        )
    }

    @CanRetrieveProjectReport
    @Transactional
    @ExceptionWrapper(GetListOfCertificateException::class)
    override fun listCertificates(projectId: Long, reportId: Long, pageable: Pageable): Page<PartnerReportCertificate> {
        val partnerIds = partnerPersistence.findTop50ByProjectId(projectId).mapTo(HashSet()) { it.id }
        val pageableDefault = if (pageable.sort.isSorted) pageable else PageRequest.of(pageable.pageNumber, pageable.pageSize, defaultSort(reportId))
        return projectReportCertificatePersistence.listCertificates(partnerIds, pageable = pageableDefault)
    }

}
