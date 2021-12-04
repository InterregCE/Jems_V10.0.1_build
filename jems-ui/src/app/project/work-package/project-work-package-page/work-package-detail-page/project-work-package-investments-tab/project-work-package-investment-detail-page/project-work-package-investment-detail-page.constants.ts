import {Validators} from '@angular/forms';
import {AppControl} from '@common/components/section/form/app-control';

export class ProjectWorkPackageInvestmentDetailPageConstants {

  public static TITLE: AppControl = {
    name: 'title',
    maxLength: 50,
    validators: [Validators.maxLength(50)]
  };

  public static EXPECTED_DELIVERY_PERIOD: AppControl = {
    name: 'expectedDeliveryPeriod'
  }

  public static JUSTIFICATION_EXPLANATION: AppControl = {
    name: 'justificationExplanation',
    maxLength: 2000,
    validators: [Validators.maxLength(2000)]
  };

  public static JUSTIFICATION_TRANSNATIONAL_RELEVANCE: AppControl = {
    name: 'justificationTransactionalRelevance',
    maxLength: 2000,
    validators: [Validators.maxLength(2000)]
  };

  public static JUSTIFICATION_BENEFITS: AppControl = {
    name: 'justificationBenefits',
    maxLength: 2000,
    validators: [Validators.maxLength(2000)]
  };

  public static JUSTIFICATION_PILOT: AppControl = {
    name: 'justificationPilot',
    maxLength: 2000,
    validators: [Validators.maxLength(2000)]
  };

  public static RISK: AppControl = {
    name: 'risk',
    maxLength: 2000,
    validators: [Validators.maxLength(2000)]
  };

  public static DOCUMENTATION: AppControl = {
    name: 'documentation',
    maxLength: 2000,
    validators: [Validators.maxLength(2000)]
  };

  public static DOCUMENTATION_EXPECTED_IMPACTS: AppControl = {
    name: 'documentationExpectedImpacts',
    maxLength: 2000,
    validators: [Validators.maxLength(2000)]
  };

  public static OWNERSHIP_SITE_LOCATION: AppControl = {
    name: 'ownershipSiteLocation',
    maxLength: 500,
    validators: [Validators.maxLength(500)]
  };

  public static OWNERSHIP_MAINTENANCE: AppControl = {
    name: 'ownershipMaintenance',
    maxLength: 2000,
    validators: [Validators.maxLength(2000)]
  };

  public static OWNERSHIP_RETAIN: AppControl = {
    name: 'ownershipRetain',
    maxLength: 500,
    validators: [Validators.maxLength(500)]
  };
}
