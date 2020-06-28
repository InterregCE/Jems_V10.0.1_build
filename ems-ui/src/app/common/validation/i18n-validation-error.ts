import {I18nFieldError} from './i18n-field-error';

export class I18nValidationError {
  i18nKey?: string;
  i8nArguments?: string[];
  httpStatus: number;
  i18nFieldErrors?: { [key: string]: I18nFieldError };
  additionalInfo?: string;
}
