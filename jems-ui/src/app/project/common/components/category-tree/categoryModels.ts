import {I18nMessage} from '@common/models/I18nMessage';

export class CategoryInfo {
  type?: any;
  id?: number;
}

export class CategoryNode {
  info?: any;
  name?: I18nMessage;
  disabled?: boolean;
  parent?: CategoryNode;
  children?: CategoryNode[];
}
