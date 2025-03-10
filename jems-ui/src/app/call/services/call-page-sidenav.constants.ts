export class CallPageSideNavConstants {

  public static FLAT_RATES = (callId: number) => {
    return {
      headline: {i18nKey: 'call.detail.budget.settings'},
      route: `/app/call/detail/${callId}/budgetSettings`,
    };
  };

  public static APPLICATION_FORM_CONFIGURATION = (callId: number) =>  {
    return {
      headline: {i18nKey: 'call.detail.application.form.config.title'},
      route: `/app/call/detail/${callId}/applicationFormConfiguration`,
    };
  };

  public static CHECKLISTS = (callId: number) =>  {
    return {
      headline: {i18nKey: 'call.detail.checklists.title'},
      route: `/app/call/detail/${callId}/checklists`,
    };
  };

  public static PRE_SUBMISSION_CHECK_SETTINGS = (callId: number) =>  {
    return {
      headline: {i18nKey: 'call.detail.pre.submission.check.config.title'},
      route: `/app/call/detail/${callId}/preSubmissionCheckSettings`,
    };
  };

  public static NOTIFICATIONS_SETTINGS = (callId: number) =>  {
    return {
      headline: {i18nKey: 'call.detail.notifications.config.title'},
      route: `/app/call/detail/${callId}/notificationSettings`,
    };
  };

  public static TRANSLATION_SETTINGS = (callId: number) =>  {
    return {
      headline: {i18nKey: 'call.detail.translations.title'},
      route: `/app/call/detail/${callId}/translationSettings`,
    };
  };
}
