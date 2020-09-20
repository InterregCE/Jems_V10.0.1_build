import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot} from '@angular/router';
import {Subject} from 'rxjs';
import {filter, tap} from 'rxjs/operators';
import {ProjectStore} from './project-store.service';

@Injectable()
export class ProjectAcronymBreadcrumbProvider implements CanActivate {
  private projectBreadcrumb$: Subject<string>;

  constructor(private projectStore: ProjectStore) {
    this.projectStore.getAcronym()
      .pipe(
        filter(() => !!this.projectBreadcrumb$),
        tap(acronym => this.projectBreadcrumb$.next(acronym))
      ).subscribe();
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    this.projectBreadcrumb$ = route.data?.breadcrumb$;
    return true;
  }
}
