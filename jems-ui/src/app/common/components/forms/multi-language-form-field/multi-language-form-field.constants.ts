export class MultiLanguageFormFieldConstants {

  public static FORM_CONTROL_NAMES = {
    inputs: 'inputs',
    language: 'language',
    translation: 'translation',
  };

  public static FORM_ERRORS = {
    translation: {
      maxlength: 'common.error.field.max.length'
    }
  };

  public static translationFormErrorArgs(currentLength: number, maxLength: number): { [p: string]: {} } {
    return {
      maxlength: {currentLength, maxLength}
    };
  }
}
