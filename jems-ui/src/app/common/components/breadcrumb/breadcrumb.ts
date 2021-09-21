import {Observable} from 'rxjs';
import {QueryParamsHandling} from '@angular/router';

export interface Breadcrumb {
  url?: string;
  i18nKey?: string;
  dynamicValue?: Observable<string>;
  queryParamsHandling?: QueryParamsHandling;
}
