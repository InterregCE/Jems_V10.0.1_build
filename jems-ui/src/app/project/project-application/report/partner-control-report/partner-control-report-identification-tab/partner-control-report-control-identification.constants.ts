export class PartnerControlReportControlIdentificationConstants {

  public static FORM_CONTROL_NAMES = {
    designatedController: 'designatedController',
    controlInstitution: 'controlInstitution',
    controllingUserId: 'controllingUserId',
    controllingUserName: 'controllingUserName',
    jobTitle: 'jobTitle',
    divisionUnit: 'divisionUnit',
    address: 'address',
    country: 'country',
    countryCode: 'countryCode',
    countrySearch: 'countrySearch',
    telephone: 'telephone',
    controllerReviewerId: 'controllerReviewerId',
    controllerReviewerName: 'controllerReviewerName',

    reportVerification: 'reportVerification',
    administrativeVerification: 'administrativeVerification',
    onTheSpotVerification: 'onTheSpotVerification',
    generalMethodologies: 'generalMethodologies',
    verificationInstances: 'verificationInstances',
    verificationFrom: 'verificationFrom',
    verificationTo: 'verificationTo',
    verificationFocus: 'verificationFocus',
    premisesOfProjectPartner: 'premisesOfProjectPartner',
    projectEvent: 'projectEvent',
    placeOfProjectOutput: 'placeOfProjectOutput',
    virtual: 'virtual',
    riskBasedVerificationApplied: 'riskBasedVerificationApplied',
    riskBasedVerificationDescription: 'riskBasedVerificationDescription'
  };
  public static JOB_TITLE_MAX_LENGTH = 50;
  public static DIVISION_MAX_LENGTH = 100;
  public static ADDRESS_MAX_LENGTH = 100;
  public static PHONE_MAX_LENGTH = 25;
  public static SAMPLING_METHODOLOGY_MAX_LENGTH = 5000;
  public static FOCUS_MAX_LENGTH = 3000;
  public static telephoneErrors = {
    pattern: 'project.contact.telephone.wrong.format'
  };
}
