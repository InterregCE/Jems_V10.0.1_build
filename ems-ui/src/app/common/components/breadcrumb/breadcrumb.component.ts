import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {Breadcrumb} from '@common/components/breadcrumb/breadcrumb';

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss'],
})
export class BreadcrumbComponent implements OnInit, OnDestroy {

  private breadcrumbs$: Subscription;
  breadcrumbs: Breadcrumb[] = [];

  constructor(private _route: ActivatedRoute,
              private _router: Router) {
    this.breadcrumbs = []
  }

  ngOnInit(): void {
    this.breadcrumbs$ = this._route.data
      .subscribe(() => this.breadcrumbs = this.buildBreadcrumbs(this._route.root, '/'))
  }

  private buildBreadcrumbs(route: ActivatedRoute, url: string, previous: Breadcrumb[] = []): Breadcrumb[] {
    let newBreadcrumbs = previous;
    let nextUrl = url;

    const data = route.routeConfig?.data
    const dynamicValue = route.snapshot.data.dynamicValue

    if (data) {
      nextUrl = `${url}/${this.extractPathFrom(route)}`
      if (!data.skipBreadcrumb) {
        const breadcrumb = {
          i18nKey: !(data.dynamicValue) ? data.breadcrumb : null,
          label: (data.dynamicValue) ? dynamicValue : null,
          url: nextUrl
        }
        newBreadcrumbs = [...newBreadcrumbs, breadcrumb];
      }
    }

    if (route.firstChild) {
      return this.buildBreadcrumbs(route.firstChild, nextUrl, newBreadcrumbs)
    }

    return newBreadcrumbs;
  }

  private extractPathFrom(currentRoute: ActivatedRoute): string {
    let path = '';
    if (currentRoute.routeConfig?.path) // this is always true
      path = currentRoute.routeConfig?.path

    const params = currentRoute.snapshot.params;
    // replace parameter placeholders
    Object.entries(params)
      .forEach(([key, value]) => path = path?.replace(`:${key}`, value))

    return path;
  }

  ngOnDestroy(): void {
    if (this.breadcrumbs$)
      this.breadcrumbs$.unsubscribe();
  }

}
