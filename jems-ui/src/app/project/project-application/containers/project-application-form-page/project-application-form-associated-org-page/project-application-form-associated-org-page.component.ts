import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, mergeMap, startWith, take, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {ProjectAssociatedOrganizationService} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {Permission} from '../../../../../security/permissions/permission';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';

@Component({
  selector: 'app-project-application-form-associated-org-page',
  templateUrl: './project-application-form-associated-org-page.component.html',
  styleUrls: ['./project-application-form-associated-org-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormAssociatedOrgPageComponent {
  Permission = Permission;

  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  associatedOrganizationsPage$ =
    combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith({active: 'sortNumber', direction: 'asc'}),
        map(sort => sort?.direction ? sort : {active: 'sortNumber', direction: 'asc'}),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.projectVersionStore.currentRouteVersion$
    ])
      .pipe(
        mergeMap(([pageIndex, pageSize, sort, version]) =>
          this.projectAssociatedOrganizationService.getAssociatedOrganizations(this.projectId, pageIndex, pageSize, undefined, version)),
        tap(page => Log.info('Fetched the project associated organizations:', this, page.content)),
      );

  constructor(public projectStore: ProjectStore,
              private projectAssociatedOrganizationService: ProjectAssociatedOrganizationService,
              private activatedRoute: ActivatedRoute,
              private projectVersionStore: ProjectVersionStore) {
  }

  deleteAssociatedOrganization(associatedOrganizationId: number): void {
    this.projectAssociatedOrganizationService.deleteAssociatedOrganization(associatedOrganizationId, this.projectId)
      .pipe(
        take(1),
        tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        tap(() => Log.info('Deleted associated organization: ', this, associatedOrganizationId)),
      ).subscribe();
  }
}
