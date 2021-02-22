import {I18nMessage} from './I18nMessage';
import {ErrorDetail} from './ErrorDetail';

export interface APIError {
  id: string;
  code: string;
  i18nMessage: I18nMessage;
  details: Array<ErrorDetail>;
  formErrors: { [key: string]: I18nMessage; };
  message: string;
}
