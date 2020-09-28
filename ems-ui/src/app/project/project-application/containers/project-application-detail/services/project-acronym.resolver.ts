import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs';
import {map, take} from 'rxjs/operators';
import {ProjectStore} from './project-store.service';

@Injectable()
export class ProjectAcronymResolver implements Resolve<string> {

  constructor(private projectStore: ProjectStore) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<string> {
    this.projectStore.init(route.params.projectId);
    return this.projectStore
      .getProjectById()
      .pipe(
        map(call => call.acronym),
        take(1)
      );
  }
}
