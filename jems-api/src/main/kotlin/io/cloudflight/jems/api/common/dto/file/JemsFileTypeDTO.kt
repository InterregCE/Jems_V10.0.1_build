package io.cloudflight.jems.api.common.dto.file

enum class JemsFileTypeDTO {
    Payment,
      Regular,
        PaymentAttachment,
      Advance,
        PaymentAdvanceAttachment,
      Ec,
        PaymentToEcAttachment,
      Audit,
         PaymentToEcAuditExport,
         PaymentAuditAttachment,

    Project,
      Report,
        Corrections,
          AuditControl,
        ProjectReport,
          ProjectResult,
          WorkPlanProjectReport,
            WorkPackageProjectReport,
              ActivityProjectReport,
                DeliverableProjectReport,
              OutputProjectReport,
        ProjectReportVerification,
          VerificationDocument,
          VerificationCertificate,

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
