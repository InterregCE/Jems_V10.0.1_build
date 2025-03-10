package io.cloudflight.jems.server.common.file.service.model

enum class JemsFileType(
    private val parent: JemsFileType?,
    private val needsId: Boolean,
    private val topParentBucket: String? = null,
) {
    Payment(null, false, "payment"),
      Regular(Payment, true),
        PaymentAttachment(Regular, false),
      Advance(Payment, true),
        PaymentAdvanceAttachment(Advance, false),
      Ec(Payment, true),
        PaymentToEcAttachment(Ec, false),
      Audit(Payment, false),
        PaymentToEcAuditExport(Audit, false),
        PaymentAuditAttachment(Audit, false),
      Account(Payment, true),
        PaymentAccountAttachment(Account, false),

    Project(null, true, "application"),
      Report(Project, false),
        Corrections(Report, false),
            AuditControl(Corrections, true),

        ProjectReport(Report, true),
          ProjectResult(ProjectReport, true),
          WorkPlanProjectReport(ProjectReport, false),
            WorkPackageProjectReport(WorkPlanProjectReport, true),
              ActivityProjectReport(WorkPackageProjectReport, true),
                DeliverableProjectReport(ActivityProjectReport, true),
              OutputProjectReport(WorkPackageProjectReport, true),

          ProjectReportVerification(ProjectReport, false),
            VerificationDocument(ProjectReportVerification, false),
            VerificationCertificate(ProjectReportVerification, false),

        Partner(Report, true),
          PartnerReport(Partner, true),

            WorkPlan(PartnerReport, false),
              WorkPackage(WorkPlan, true),
                Activity(WorkPackage, true),
                  Deliverable(Activity, true),
                Output(WorkPackage, true),

            Expenditure(PartnerReport, true),
            Procurement(PartnerReport, true),
              ProcurementAttachment(Procurement, false),
              ProcurementGdprAttachment(Procurement, false),
            Contribution(PartnerReport, true),

          PartnerControlReport(Partner, true),
            ControlDocument(PartnerControlReport, false),
            ControlCertificate(PartnerControlReport, false),
            ControlReport(PartnerControlReport, false),

      Contracting(Project, false),
        ContractSupport(Contracting, false),
          Contract(ContractSupport, false),
          ContractDoc(ContractSupport, false),
        ContractPartner(Contracting, false),
          ContractPartnerDoc(ContractPartner, true),
        ContractInternal(Contracting, false),

      SharedFolder(Project, false),

    CallTranslation(null, true, "jems-translation-file-bucket"),
    CallTranslationArchive(null, true, "jems-translation-file-bucket");

    fun generatePath(vararg ids: Long): String {
        if (this.parent == null)
            return if (this.needsId) "$name/${ids.last().toFixedLength()}/" else "$name/"

        return if (this.needsId)
            parent.generatePath(*ids.dropLast(1).toLongArray()) +
                "${this.name}/" +
                "${ids.last().toFixedLength()}/"
        else
            parent.generatePath(*ids) +
                "${this.name}/"
    }

    fun getBucket(): String {
        if (topParentBucket != null) {
            return topParentBucket
        }
        return parent!!.getBucket()
    }

    private fun Long.toFixedLength() = this.toString().padStart(6, '0')

    fun getItsIdFrom(path: String): Long? {
        if (!needsId)
            return null

        return Regex("$name/(\\d+)").find(path)!!.groups.get(1)!!.value.toLong()
    }

    fun isSubFolderOf(subPath: JemsFileType): Boolean {
        if (parent == null)
            return false

        return parent == subPath || parent.isSubFolderOf(subPath)
    }

}
