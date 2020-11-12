import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, RouterStateSnapshot} from '@angular/router';
import {Observable, of} from 'rxjs';
import {ProjectStore} from './project-store.service';

@Injectable()
export class ProjectAcronymResolver implements Resolve<Observable<string>> {

  constructor(private projectStore: ProjectStore) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Observable<string>> {
    return of(this.projectStore.getAcronym());
  }
}
