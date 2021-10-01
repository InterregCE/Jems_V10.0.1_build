export class ProjectPartnerCoFinancingTabConstants {

  public static MAX_NUMBER_OF_PARTNER_CONTRIBUTIONS = 10;
  public static MAX_NUMBER_OF_FINANCES = 4;

  public static FORM_CONTROL_NAMES = {
    partnerContributions: 'partnerContributions',
    name: 'name',
    status: 'status',
    amount: 'amount',
    partner: 'partner',
    finances: 'finances',
    fundPercentage: 'percentage',
    fundId: 'fundId',
    fundAmount: 'fundAmount',
    partnerAmount: 'partnerAmount',
    partnerPercentage: 'partnerPercentage'
  };

  public static FORM_ERRORS = {

    fundIdErrors: {
      required: 'project.partner.coFinancing.fundId.should.not.be.empty',
    },
    fundRateTotalErrors: {
      min: 'project.partner.coFinancing.fundRateTotal.should.not.exceed.100',
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
    }
  };

}
