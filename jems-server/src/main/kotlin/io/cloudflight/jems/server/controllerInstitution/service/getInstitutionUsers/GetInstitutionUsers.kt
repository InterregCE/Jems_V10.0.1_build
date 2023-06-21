package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionUsers

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetInstitutionUsers(
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence,
): GetInstitutionUsersInteractor {
    @CanViewPartnerReport
    @ExceptionWrapper(GetInstitutionUsersException::class)
    @Transactional(readOnly = true)
    override fun getInstitutionUsers(partnerId: Long, institutionId: Long): List<UserSimple> =
        controllerInstitutionPersistence.getControllerUsersForReportByInstitutionId(institutionId)
}
