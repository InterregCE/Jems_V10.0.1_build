import {AuditControlDTO} from '@cat/api';

export class AuditControlConstants {
  public static CONTROLLING_BODY = [
    AuditControlDTO.ControllingBodyEnum.Controller,
    AuditControlDTO.ControllingBodyEnum.NationalApprobationBody,
    AuditControlDTO.ControllingBodyEnum.RegionalApprobationBody,
    AuditControlDTO.ControllingBodyEnum.JS,
    AuditControlDTO.ControllingBodyEnum.MA,
    AuditControlDTO.ControllingBodyEnum.NA,
    AuditControlDTO.ControllingBodyEnum.GoA,
    AuditControlDTO.ControllingBodyEnum.AA,
    AuditControlDTO.ControllingBodyEnum.EC,
    AuditControlDTO.ControllingBodyEnum.ECA,
    AuditControlDTO.ControllingBodyEnum.OLAF,
  ];

  public static CONTROL_TYPES = [
    AuditControlDTO.ControlTypeEnum.Administrative,
    AuditControlDTO.ControlTypeEnum.OnTheSpot,
  ];

  public static MIN_VALUE = -999_999_999.99;
  public static MAX_VALUE = 999_999_999.99;
  public static COMMENT_LENGTH = 2000;

  public static FORM_CONTROLS = {
    controllingBody: 'controllingBody',
    controlType: 'controlType',
    startDate: 'startDate',
    endDate: 'endDate',
    finalReportDate: 'finalReportDate',
    totalControlledAmount: 'totalControlledAmount',
    totalCorrectionsAmount: 'totalCorrectionsAmount',
    comment: 'comment',
  };
}
