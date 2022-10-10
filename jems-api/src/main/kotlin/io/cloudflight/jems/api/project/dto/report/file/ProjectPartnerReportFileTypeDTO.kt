package io.cloudflight.jems.api.project.dto.report.file

enum class ProjectPartnerReportFileTypeDTO {
    Payment,
      PaymentAttachment,

    Project,
      Report,
        Partner,
          PartnerReport,

            WorkPlan,
              WorkPackage,
                Activity,
                  Deliverable,
                Output,

            Expenditure,
            Procurement,
              ProcurementAttachment,
            Contribution,

          PartnerControlReport,
            ControlDocument,

      Contracting,
        ContractSupport,
          Contract,
          ContractDoc,
        ContractPartner,
          ContractPartnerDoc,
        ContractInternal,
}
