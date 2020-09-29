import {Observable} from 'rxjs';

export interface Breadcrumb {
  url?: string;
  i18nKey?: string;
  dynamicValue?: Observable<string>;
}
