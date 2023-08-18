package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

import java.math.BigDecimal

interface ExpenditureCostAfterSubmission : ExpenditureCostWithCategory {
    var declaredAmountAfterSubmission: BigDecimal?
}
