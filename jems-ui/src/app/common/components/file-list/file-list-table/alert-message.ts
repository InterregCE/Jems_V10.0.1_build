import {Alert} from '@common/components/forms/alert';

export interface AlertMessage {
  id: string;
  type: Alert;
  i18nMessage: string;
  i18nArgs: any;
}
