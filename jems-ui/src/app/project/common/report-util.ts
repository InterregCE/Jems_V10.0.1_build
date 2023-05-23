import {ProjectPartnerReportDTO} from '@cat/api';

export class ReportUtil {

  static isPartnerReportSubmittable(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return [
      ProjectPartnerReportDTO.StatusEnum.Draft,
      ProjectPartnerReportDTO.StatusEnum.ReOpenSubmittedLast,
      ProjectPartnerReportDTO.StatusEnum.ReOpenSubmittedLimited,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLimited,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLast
    ].includes(status);
  }

  static isPartnerReportReopened(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return [
      ProjectPartnerReportDTO.StatusEnum.ReOpenSubmittedLast,
      ProjectPartnerReportDTO.StatusEnum.ReOpenSubmittedLimited,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLimited,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLast
    ].includes(status);
  }

  static isControlReportExists(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return [
      ProjectPartnerReportDTO.StatusEnum.Certified,
      ProjectPartnerReportDTO.StatusEnum.InControl,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLimited,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLast
    ].includes(status);
  }

  static isControlReportOpen(status: ProjectPartnerReportDTO.StatusEnum): boolean {
    return [
      ProjectPartnerReportDTO.StatusEnum.InControl,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLimited,
      ProjectPartnerReportDTO.StatusEnum.ReOpenInControlLast
    ].includes(status);
  }
}
