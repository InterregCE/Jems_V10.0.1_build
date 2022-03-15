import {AppControl} from '@common/components/section/form/app-control';

export class PartnerReportWorkplanConstants {

  public static WORK_PACKAGES: AppControl = {
    name: 'workPackages'
  };

  public static ACTIVITIES: AppControl = {
    name: 'activities'
  };

  public static OUTPUTS: AppControl = {
    name: 'outputs'
  };

  public static DELIVERABLES: AppControl = {
    name: 'deliverables'
  };

  public static WORK_PACKAGE_DESCRIPTION: AppControl = {
    name: 'description',
    maxLength: 2000,
  };

  public static ACTIVITY_PROGRESS: AppControl = {
    name: 'progress',
    maxLength: 2000,
  };

  public static ACTIVITY_TITLE: AppControl = {
    name: 'title'
  };

  public static DELIVERABLE_TITLE: AppControl = {
    name: 'title',
  };

  public static DELIVERABLE_CONTRIBUTION: AppControl = {
    name: 'contribution',
  };

  public static DELIVERABLE_EVIDENCE: AppControl = {
    name: 'evidence',
  };

  public static OUTPUT_TITLE: AppControl = {
    name: 'title',
  };

  public static OUTPUT_CONTRIBUTION: AppControl = {
    name: 'contribution',
  };

  public static OUTPUT_EVIDENCE: AppControl = {
    name: 'evidence',
  };

}
