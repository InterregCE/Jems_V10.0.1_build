export class ProjectPartnerCoFinancingTabConstants {

  public static MAX_NUMBER_OF_PARTNER_CONTRIBUTIONS = 10;
  private static PERCENTAGE_ERROR_TRANSLATION_KEY = 'project.partner.coFinancing.percentage.invalid';

  public static FORM_CONTROL_NAMES = {
    partnerContributions: 'partnerContributions',
    name: 'name',
    status: 'status',
    amount: 'amount',
    isPartner: 'isPartner',
    fundPercentage: 'fundPercentage',
    fundId: 'fundId',
    fundAmount: 'fundAmount',
    partnerAmount: 'partnerAmount',
    partnerPercentage: 'partnerPercentage',
    additionalFundId: 'additionalFundId',
    additionalFundAmount: 'additionalFundAmount',
    additionalFundPercentage: 'additionalFundPercentage',
  };

  public static FORM_ERRORS = {

    fundIdErrors: {
      required: 'project.partner.coFinancing.fundId.should.not.be.empty',
    },
    percentageErrors: {
      required: ProjectPartnerCoFinancingTabConstants.PERCENTAGE_ERROR_TRANSLATION_KEY,
    },
    partnerContributionNameErrors: {
      required: 'project.partner.coFinancing.contribution.origin.name.required',
    },
    partnerContributionStatusErrors: {
      required: 'project.partner.coFinancing.contribution.origin.amount.required',
    },
    partnerContributionAmountErrors: {
      required: 'project.partner.coFinancing.contribution.origin.amount.required',
      min: 'project.partner.coFinancing.contribution.origin.amount.min.invalid',
    },
    partnerContributionErrors: {
      total: 'project.partner.coFinancing.contribution.origin.total.invalid',
      maxlength: 'project.partner.coFinancing.contribution.origin.max.length',
    },
    additionalFundIdErrors: {
      required: 'project.partner.coFinancing.fundId.should.not.be.empty',
    },
    additionalFundPercentageErrors: {
      required: ProjectPartnerCoFinancingTabConstants.PERCENTAGE_ERROR_TRANSLATION_KEY,
    }
  };

}
