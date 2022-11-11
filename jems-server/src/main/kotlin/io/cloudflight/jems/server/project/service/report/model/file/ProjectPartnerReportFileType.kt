package io.cloudflight.jems.server.project.service.report.model.file

enum class ProjectPartnerReportFileType(
    private val parent: ProjectPartnerReportFileType?,
    private val needsId: Boolean,
    private val topParentBucket: String? = null,
) {
    Payment(null, false, "payment"),
      Regular(Payment, true),
        PaymentAttachment(Regular, false),
      Advance(Payment, true),
        PaymentAdvanceAttachment(Advance, false),

    Project(null, true, "application"),
      Report(Project, false),
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
            Contribution(PartnerReport, true),

          PartnerControlReport(Partner, true),
            ControlDocument(PartnerControlReport, false),

      Contracting(Project, false),
        ContractSupport(Contracting, false),
          Contract(ContractSupport, false),
          ContractDoc(ContractSupport, false),
        ContractPartner(Contracting, false),
          ContractPartnerDoc(ContractPartner, true),
        ContractInternal(Contracting, false);

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

}
