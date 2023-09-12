import {ProjectPartnerReportDTO, ProjectReportDTO, ProjectReportSummaryDTO} from '@cat/api';

export class ReportUtil {

  // Partner Report
  static isPartnerReportSubmittable(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return [
      ProjectPartnerReportDTO.StatusEnum.Draft,
      ProjectPartnerReportDTO.StatusEnum.ReOpenSubmittedLast,
      ProjectPartnerReportDTO.StatusEnum.ReOpenSubmittedLimited,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLimited,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLast
    ].includes(status);
  }

  static isPartnerReportSubmittedOrAfter(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return [
      ProjectPartnerReportDTO.StatusEnum.Submitted,
      ProjectPartnerReportDTO.StatusEnum.InControl,
      ProjectPartnerReportDTO.StatusEnum.ReOpenCertified,
      ProjectPartnerReportDTO.StatusEnum.Certified,
    ].includes(status)
  }

  static isControlReportExists(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return [
      ProjectPartnerReportDTO.StatusEnum.InControl,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLast,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLimited,
      ProjectPartnerReportDTO.StatusEnum.Certified,
      ProjectPartnerReportDTO.StatusEnum.ReOpenCertified,
    ].includes(status);
  }

  static isControlReportOpen(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return [
      ProjectPartnerReportDTO.StatusEnum.InControl,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLimited,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLast
    ].includes(status);
  }

  static isVerificationReportOpen(status: ProjectReportDTO.StatusEnum): boolean {
    return [
      ProjectReportDTO.StatusEnum.InVerification,
    ].includes(status);
  }

  static isControlCertifiedReOpened(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return status === ProjectPartnerReportDTO.StatusEnum.ReOpenCertified;
  }

  static isReopenedPartnerReportLast(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return [
      ProjectPartnerReportDTO.StatusEnum.ReOpenSubmittedLast,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLast
    ].includes(status);
  }

  static isReopenedPartnerReportLimited(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return [
      ProjectPartnerReportDTO.StatusEnum.ReOpenSubmittedLimited,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLimited
    ].includes(status);
  }

  static controlFinalized(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return status === ProjectPartnerReportDTO.StatusEnum.Certified;
  }

  static controlCanBeFinalized(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return status === ProjectPartnerReportDTO.StatusEnum.InControl
      || status === ProjectPartnerReportDTO.StatusEnum.ReOpenCertified;
  }

  // Project Report
  static isProjectReportOpen(status: ProjectReportSummaryDTO.StatusEnum): boolean {
    return status === ProjectReportSummaryDTO.StatusEnum.Draft;
  }

  static isProjectReportSubmitted(status: ProjectReportSummaryDTO.StatusEnum): boolean {
    return status === ProjectReportSummaryDTO.StatusEnum.Submitted;
  }

  static isProjectReportAfterVerificationStarted(status: ProjectReportSummaryDTO.StatusEnum): boolean {
    return [ProjectReportSummaryDTO.StatusEnum.InVerification, ProjectReportSummaryDTO.StatusEnum.Finalized].includes(status);
  }

  static isProjectReportVerificationOngoing(status: ProjectReportSummaryDTO.StatusEnum): boolean {
    return [ProjectReportSummaryDTO.StatusEnum.InVerification].includes(status);
  }
}
