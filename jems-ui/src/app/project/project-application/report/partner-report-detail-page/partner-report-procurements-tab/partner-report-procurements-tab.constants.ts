import {AppControl} from '@common/components/section/form/app-control';
import {Validators} from '@angular/forms';

export class PartnerReportProcurementsTabConstants {
  public static MIN_VALUE = 0;
  public static MAX_NUMBER_OF_ITEMS = 50;

  public static PROCUREMENTS: AppControl = {
    name: 'procurements'
  };

  public static CONTRACT_ID: AppControl = {
    name: 'contractId',
    maxLength: 30,
    validators: [Validators.maxLength(30), Validators.required]
  };

  public static CONTRACT_TYPE: AppControl = {
    name: 'contractType',
    maxLength: 30,
    validators: [Validators.maxLength(30)]
  };

  public static CONTRACT_AMOUNT: AppControl = {
    name: 'contractAmount',
  };

  public static CURRENCY: AppControl = {
    name: 'currencyCode',
  };

  public static SUPPLIER_NAME: AppControl = {
    name: 'supplierName',
    maxLength: 30,
    validators: [Validators.maxLength(30)]
  };

  public static COMMENT: AppControl = {
    name: 'comment',
    maxLength: 2000,
    validators: [Validators.maxLength(2000)]
  };

  public static COMMENT_PREVIEW: AppControl = {
    name: 'commentPreview',
    maxLength: 2000,
    validators: [Validators.maxLength(2000)]
  };

  public static ATTACHMENT: AppControl = {
    name: 'attachment',
  };

}
