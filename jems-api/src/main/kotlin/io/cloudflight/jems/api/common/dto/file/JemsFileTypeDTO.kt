package io.cloudflight.jems.api.common.dto.file

enum class JemsFileTypeDTO {
    Payment,
      Regular,
        PaymentAttachment,
      Advance,
        PaymentAdvanceAttachment,
      Ec,
        PaymentToEcAttachment,

    Project,
      Report,
        ProjectReport,
          ProjectResult,
          WorkPlanProjectReport,
            WorkPackageProjectReport,
              ActivityProjectReport,
                DeliverableProjectReport,
              OutputProjectReport,
        ProjectReportVerification,
          VerificationDocument,
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
              ProcurementGdprAttachment,
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
