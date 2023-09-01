import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ActivatedRoute, NavigationExtras} from '@angular/router';
import {Breadcrumb} from '@common/components/breadcrumb/breadcrumb';
import {takeUntil, tap} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {RoutingService} from '@common/services/routing.service';
import {Title} from '@angular/platform-browser';
import {CustomTranslatePipe} from '@common/pipe/custom-translate-pipe';
import {Observable, of} from 'rxjs';

@Component({
  selector: 'jems-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BreadcrumbComponent extends BaseComponent implements OnInit {

  breadcrumbs: Breadcrumb[] = [];

  constructor(private route: ActivatedRoute,
              private routingService: RoutingService,
              private titleService: Title,
              private customTranslatePipe: CustomTranslatePipe) {
    super();
  }

  ngOnInit(): void {
    this.route.data
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.breadcrumbs = this.buildBreadcrumbs(this.route.root, '/'))
      ).subscribe();
    this.getTitleObsservable()
      .subscribe((title) => this.titleService.setTitle(title));
  }

  navigate(url: string, extras: NavigationExtras): void {
    this.routingService.navigate([url], extras);
  }

  private buildBreadcrumbs(route: ActivatedRoute,
                           url: string,
                           previous: Breadcrumb[] = [],
                           paramsHandling?: string): Breadcrumb[] {
    let newBreadcrumbs = previous;
    let nextUrl = url;

    const data = route.routeConfig?.data;
    const queryParamsHandling = data?.queryParamsHandling || paramsHandling || '';
    if (data) {
      nextUrl = `${url}/${this.extractPathFrom(route)}`;
      if (!data.skipBreadcrumb) {
        const breadcrumb = {
          i18nKey: !data.dynamicBreadcrumb && data.breadcrumb,
          dynamicValue: data.dynamicBreadcrumb && route.snapshot?.data?.breadcrumb$,
          url: nextUrl,
          queryParamsHandling
        };
        newBreadcrumbs = [...newBreadcrumbs, breadcrumb];
      }
    }

    if (route.firstChild) {
      return this.buildBreadcrumbs(route.firstChild, nextUrl, newBreadcrumbs, queryParamsHandling);
    }

    return newBreadcrumbs;
  }

  private getTitleObsservable(): Observable<string> {
    const breadcrumb = this.breadcrumbs[this.breadcrumbs.length - 1];
    if (breadcrumb.dynamicValue) {return breadcrumb.dynamicValue;}
    else if (breadcrumb.i18nKey) {return this.customTranslatePipe.translateService.get(breadcrumb.i18nKey);}
    else {return of('Jems');}
  }

  private extractPathFrom(currentRoute: ActivatedRoute): string {
    let path = '';
    if (currentRoute.routeConfig?.path) { // this is always true
      path = currentRoute.routeConfig?.path;
    }

    const params = currentRoute.snapshot.params;
    // replace parameter placeholders
    Object.entries(params)
      .forEach(([key, value]) => path = path?.replace(`:${key}`, value));

    return path;
  }
}
