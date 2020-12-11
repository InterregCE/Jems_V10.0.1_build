package io.cloudflight.jems.server.project.service.workpackage.investment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.mockk.impl.annotations.MockK
import org.springframework.data.domain.PageRequest
import java.util.*


abstract class UnitTestWorkPackageInvestmentBase : UnitTest() {

    protected val projectId = 1L
    protected val workPackageId = 2L
    protected val pageRequest = PageRequest.of(1, 2)
    protected val workPackageInvestmentId: Long = Math.random().toLong()


    @MockK
    lateinit var persistence: WorkPackagePersistence

    protected fun createWorkPackageInvestment(workPackageInvestmentId: Long = Math.random().toLong()) =
        WorkPackageInvestment(
            workPackageInvestmentId,
            Math.random().toInt(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )

}
