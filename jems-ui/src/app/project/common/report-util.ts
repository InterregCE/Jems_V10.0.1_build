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

  static isReportOpenInitially(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return [
      ProjectPartnerReportDTO.StatusEnum.Draft,
    ].includes(status);
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
  static isProjectReportDraft(status: ProjectReportSummaryDTO.StatusEnum): boolean {
    return status === ProjectReportSummaryDTO.StatusEnum.Draft;
  }

  static isProjectReportOpen(status: ProjectReportSummaryDTO.StatusEnum): boolean {
    return [
      ProjectReportSummaryDTO.StatusEnum.Draft,
      ProjectReportSummaryDTO.StatusEnum.VerificationReOpenedLimited,
      ProjectReportSummaryDTO.StatusEnum.VerificationReOpenedLast,
      ProjectReportSummaryDTO.StatusEnum.ReOpenSubmittedLimited,
      ProjectReportSummaryDTO.StatusEnum.ReOpenSubmittedLast
    ].includes(status);
  }

  static isProjectReportLimitedReopened(status: ProjectReportSummaryDTO.StatusEnum): boolean {
    return status === ProjectReportSummaryDTO.StatusEnum.ReOpenSubmittedLimited || status === ProjectReportSummaryDTO.StatusEnum.VerificationReOpenedLimited;
  }

  static isProjectReportSubmitted(status: ProjectReportSummaryDTO.StatusEnum): boolean {
    return status === ProjectReportSummaryDTO.StatusEnum.Submitted;
  }

  static isProjectReportAfterVerificationStarted(status: ProjectReportSummaryDTO.StatusEnum): boolean {
    return [
      ProjectReportSummaryDTO.StatusEnum.InVerification,
      ProjectReportSummaryDTO.StatusEnum.VerificationReOpenedLimited,
      ProjectReportSummaryDTO.StatusEnum.VerificationReOpenedLast,
      ProjectReportSummaryDTO.StatusEnum.Finalized,
      ProjectReportSummaryDTO.StatusEnum.ReOpenFinalized
    ].includes(status);
  }

  static isVerificationReportOpen(status: ProjectReportDTO.StatusEnum): boolean {
    return [
      ProjectReportDTO.StatusEnum.InVerification,
      ProjectReportDTO.StatusEnum.ReOpenFinalized,
    ].includes(status);
  }

  static projectReportCanBeReopened(status: ProjectReportSummaryDTO.StatusEnum): boolean {
    return [
      ProjectReportSummaryDTO.StatusEnum.Submitted,
      ProjectReportSummaryDTO.StatusEnum.InVerification,
      ProjectReportSummaryDTO.StatusEnum.ReOpenFinalized
    ].includes(status);
  }
}
