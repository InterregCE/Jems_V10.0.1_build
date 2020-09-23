import {Observable, of} from 'rxjs';
import {Permission} from '../../../security/permissions/permission';
import {Route} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';

export class Breadcrumb {
  /**
   * A static label key for routes that will not be displayed
   */
  static DO_NOT_SHOW = 'doNotShowBreadcrumb';

  url?: string;
  i18nKey: string;
  label: Observable<string>;
  permissionsOnly?: Permission[];

  constructor(route: Route, translateService: TranslateService) {
    this.i18nKey = route.data?.breadcrumb;
    this.label = route.data?.breadcrumb$ || of(translateService.instant(this.i18nKey));
    this.permissionsOnly = route.data?.permissionsOnly;
  }
}
