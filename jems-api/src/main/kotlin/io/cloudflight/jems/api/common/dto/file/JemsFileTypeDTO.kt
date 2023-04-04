package io.cloudflight.jems.api.common.dto.file

enum class JemsFileTypeDTO {
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
