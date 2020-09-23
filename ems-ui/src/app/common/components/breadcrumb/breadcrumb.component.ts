import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ActivatedRoute, Route, Router} from '@angular/router';
import {Log} from '../../utils/log';
import {of} from 'rxjs';
import {Breadcrumb} from '@common/components/breadcrumb/breadcrumb';
import {TranslateService} from '@ngx-translate/core';
import {BaseComponent} from '@common/components/base-component';

/**
 * Displays a list of breadcrumbs depending on the route config.
 * The routes need to be configured as a tree.
 * eg:
 *    path: 'parent',
 *    data: new RouteData({
 *       breadcrumb: 'parent.breadcrumb.18nKey'
 *    }),
 *    children: [
 *    {
 *      ..parent component
 *    },
 *    {
 *       path: 'child',
 *       component: childComponent,
 *       data: new RouteData({
 *         breadcrumb: 'child.breadcrumb.18nKey',
 *         permissionsOnly: [Permission.ADMINISTRATOR, Permission.PROGRAMME_USER],
 *       }),
 *       canActivate: [AuthenticationGuard, CustomBreadcrumbProvider],
 *       children: [...]
 *     },
 *   ]
 *
 * The component will ignore a specific breadcrumb in the constructed list if:
 *   - the breadcrumb key label is equal to Breadcrumb.DO_NOT_SHOW
 *   - the breadcrumb.data.permissionsOnly don't match with the current user permissions
 *
 * In order to have a breadcrumb with dynamic data you need to create a custom provider guard
 * and add it it route's canActivate.
 */
@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BreadcrumbComponent extends BaseComponent implements OnInit {
  Breadcrumb = Breadcrumb;

  breadcrumbs: Breadcrumb[] = [];
  home: Breadcrumb = {
    i18nKey: 'common.breadcrumb.home',
    url: '/',
    label: of(this.translateService.instant('common.breadcrumb.home'))
  };

  constructor(private activatedRoute: ActivatedRoute,
              private router: Router,
              private changeDetectorRef: ChangeDetectorRef,
              private translateService: TranslateService) {
    super();
  }

  ngOnInit(): void {
    this.adaptBreadcrumbs();
  }

  private adaptBreadcrumbs(): void {
    const currentBreadcrumb = this.activatedRoute?.snapshot?.data?.breadcrumb;
    if (!currentBreadcrumb) {
      // no breadcrumb configured in the current route
      this.breadcrumbs = [];
      return;
    }
    this.router.config.find(route => {
      this.createBreadcrumbList(currentBreadcrumb, route);
      if (!this.breadcrumbs.length) {
        return false;
      }
      Log.debug('Creating breadcrumbs', this, this.breadcrumbs);
      // add the home breadcrumb as the first in the list
      this.breadcrumbs.splice(0, 0, this.home);
      this.assignPathsAndLabels();
      this.changeDetectorRef.markForCheck();
      return true;
    });
  }

  /**
   * Splits the current url into separated paths and assigns them to each breadcrumb.
   */
  private assignPathsAndLabels(): void {
    const paths = this.router.url.split('/');
    paths.forEach((path, index) =>
      this.breadcrumbs[index].url = paths.slice(0, index + 1).join('/')
    );
  }

  /**
   * Finds the route containing the current breadcrumb label and builds the list of parents.
   */
  private createBreadcrumbList(breadcrumb: string, route: Route): boolean {
    if (breadcrumb === route.data?.breadcrumb) {
      this.breadcrumbs.push(new Breadcrumb(route, this.translateService));
      return true;
    }
    if (!route.children) {
      return false;
    }

    for (const child of route.children) {
      if (this.createBreadcrumbList(breadcrumb, child)) {
        this.breadcrumbs.splice(0, 0, new Breadcrumb(route, this.translateService));
        return true;
      }
    }
    return false;
  }
}
