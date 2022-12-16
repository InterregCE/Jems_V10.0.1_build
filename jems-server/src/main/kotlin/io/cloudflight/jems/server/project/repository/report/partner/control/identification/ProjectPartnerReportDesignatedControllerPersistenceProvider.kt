package io.cloudflight.jems.server.project.repository.report.partner.control.identification

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.controllerInstitution.repository.ControllerInstitutionRepository
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportDesignatedControllerEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.partner.identification.toModel
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportDesignatedController
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportDesignatedControllerPersistence
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Repository
class ProjectPartnerReportDesignatedControllerPersistenceProvider(
    private val designatedControllerRepository: ProjectPartnerReportDesignatedControllerRepository,
    private val reportRepository: ProjectPartnerReportRepository,
    private val institutionRepository: ControllerInstitutionRepository,
    private val userRepo: UserRepository,
) : ProjectPartnerReportDesignatedControllerPersistence {
    @Transactional(readOnly = true)
    override fun getControlReportDesignatedController(partnerId: Long, reportId: Long): ReportDesignatedController =
        designatedControllerRepository.findByReportEntityIdAndReportEntityPartnerId(
            reportId = reportId,
            partnerId = partnerId,
        ).toModel()

    @Transactional
    override fun create(
        partnerId: Long,
        reportId: Long,
        institutionId: Long
    ) {
        createDesignatedControllerEntityFrom(
            entity = reportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId),
            institution = institutionRepository.findById(institutionId).get()
        )
    }

    @Transactional
    override fun updateWithInstitutionName(
        partnerId: Long,
        reportId: Long,
        institutionName: String
    ) {
        val entity = designatedControllerRepository.findByReportEntityIdAndReportEntityPartnerId(
            reportId = reportId,
            partnerId = partnerId,
        )

        entity.institutionName = institutionName
    }

    @Transactional
    override fun updateDesignatedController(
        partnerId: Long,
        reportId: Long,
        designatedController: ReportDesignatedController
    ): ReportDesignatedController {
        val entity = designatedControllerRepository.findByReportEntityIdAndReportEntityPartnerId(
            reportId = reportId,
            partnerId = partnerId,
        )

        entity.controllingUser = designatedController.controllingUserId?.let { userRepo.getById(it) }
        entity.controllerReviewer = designatedController.controllerReviewerId?.let { userRepo.getById(it) }
        entity.jobTitle = designatedController.jobTitle
        entity.address = designatedController.address
        entity.country = designatedController.country
        entity.countryCode = designatedController.countryCode
        entity.divisionUnit = designatedController.divisionUnit
        entity.telephone = designatedController.telephone

        return entity.toModel()
    }

    private fun createDesignatedControllerEntityFrom(
        entity: ProjectPartnerReportEntity,
        institution: ControllerInstitutionEntity
    ) =
        designatedControllerRepository.save(
            ProjectPartnerReportDesignatedControllerEntity(
                reportEntity = entity,
                controlInstitution = institution,
                controllingUser = null,
                controllerReviewer = null,
                jobTitle = null,
                divisionUnit = null,
                address = null,
                countryCode = null,
                country = null,
                telephone = null,
                institutionName = institution.name
            )
        )
}
