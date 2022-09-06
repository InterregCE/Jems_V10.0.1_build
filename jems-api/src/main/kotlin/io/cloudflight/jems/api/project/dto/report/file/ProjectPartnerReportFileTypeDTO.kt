package io.cloudflight.jems.api.project.dto.report.file

enum class ProjectPartnerReportFileTypeDTO {
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

      Contracting,
        ContractSupport,
          Contract,
          ContractDoc,
        ContractPartner,
          ContractPartnerDoc,
        ContractInternal,
}
