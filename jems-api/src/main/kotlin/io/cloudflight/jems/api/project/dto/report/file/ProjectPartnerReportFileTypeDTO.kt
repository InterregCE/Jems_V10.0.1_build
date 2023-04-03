package io.cloudflight.jems.api.project.dto.report.file

/**
 * TODO: Refactor this model and entity to common module.
 */
enum class ProjectPartnerReportFileTypeDTO {
    Payment,
      Regular,
        PaymentAttachment,
      Advance,
        PaymentAdvanceAttachment,

    Project,
      Report,
        ProjectReport,
          ProjectResult,
          WorkPlanProjectReport,
            WorkPackageProjectReport,
              ActivityProjectReport,
                DeliverableProjectReport,
              OutputProjectReport,
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
            ControlCertificate,
            ControlReport,

      Contracting,
        ContractSupport,
          Contract,
          ContractDoc,
        ContractPartner,
          ContractPartnerDoc,
        ContractInternal,

      SharedFolder,
}
