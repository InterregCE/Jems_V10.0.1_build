package io.cloudflight.jems.server.payments.service.advance.getContractedProjects

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrieveAdvancePayments
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSearchRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractedProjects(
    private val projectPersistence: ProjectPersistence,
): GetContractedProjectsInteractor {

    companion object {
        private fun onlyContractedProjects(id: String) = ProjectSearchRequest(
            id = id,
            acronym = null,
            firstSubmissionFrom = null,
            firstSubmissionTo = null,
            lastSubmissionFrom = null,
            lastSubmissionTo = null,
            objectives = emptySet(),
            statuses = setOf(
                ApplicationStatus.CONTRACTED,
                ApplicationStatus.IN_MODIFICATION,
                ApplicationStatus.MODIFICATION_SUBMITTED
            ),
            calls = emptySet(),
            users = emptySet(),
        )
    }

    @CanRetrieveAdvancePayments
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetContractedProjectsException::class)
    override fun getContractedProjects(searchId: String) =
         projectPersistence.getProjects(Pageable.ofSize(30), onlyContractedProjects(searchId))

}
