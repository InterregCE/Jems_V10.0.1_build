import {I18nMessage} from '@common/models/I18nMessage';

export class ConfirmDialogData {
  public title: string;
  public message?: I18nMessage | string;
  public warnMessage?: I18nMessage | string;
}
