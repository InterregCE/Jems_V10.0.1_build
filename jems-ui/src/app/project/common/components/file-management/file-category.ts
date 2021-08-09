import {I18nMessage} from '@common/models/I18nMessage';

export enum FileCategoryEnum {
  ASSESSMENT = 'ASSESSMENT',
  APPLICATION = 'APPLICATION',
  PARTNER = 'PARTNER',
  INVESTMENT = 'INVESTMENT',
  ALL = 'ALL',
}

export class FileCategoryInfo {
  type?: FileCategoryEnum | string;
  id?: number;
}

export class FileCategoryNode {
  info?: FileCategoryInfo;
  name?: I18nMessage;
  parent?: FileCategoryNode;
  children?: FileCategoryNode[];
}
