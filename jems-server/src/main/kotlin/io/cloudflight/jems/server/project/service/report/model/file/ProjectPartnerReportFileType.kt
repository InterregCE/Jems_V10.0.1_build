package io.cloudflight.jems.server.project.service.report.model.file

enum class ProjectPartnerReportFileType(
    private val parent: ProjectPartnerReportFileType?,
    val needsId: Boolean,
) {
    Project(null, true),
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

    private fun Long.toFixedLength() = this.toString().padStart(6, '0')

}
