import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, mergeMap, startWith, take, takeUntil, tap} from 'rxjs/operators';
import {Tables} from '../../../../../common/utils/tables';
import {Log} from '../../../../../common/utils/log';
import {ProjectAssociatedOrganizationService, ProjectPartnerService} from '@cat/api';
import {ProjectApplicationFormSidenavService} from '../services/project-application-form-sidenav.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {Permission} from '../../../../../security/permissions/permission';
import {BaseComponent} from '@common/components/base-component';

@Component({
  selector: 'app-project-application-form-associated-org-page',
  templateUrl: './project-application-form-associated-org-page.component.html',
  styleUrls: ['./project-application-form-associated-org-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormAssociatedOrgPageComponent extends BaseComponent {
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
      )
    ])
      .pipe(
        mergeMap(([pageIndex, pageSize, sort]) =>
          this.projectAssociatedOrganizationService.getAssociatedOrganizations(this.projectId, pageIndex, pageSize, [sort])),
        tap(page => Log.info('Fetched the project associated organizations:', this, page.content)),
      );

  constructor(public projectStore: ProjectStore,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private projectAssociatedOrganizationService: ProjectAssociatedOrganizationService,
              private activatedRoute: ActivatedRoute) {
    super();
    this.projectStore.init(this.projectId);
  }

  deleteAssociatedOrganization(associatedOrganizationId: number): void {
    this.projectAssociatedOrganizationService.deleteAssociatedOrganization(associatedOrganizationId, this.projectId)
      .pipe(
        take(1),
        takeUntil(this.destroyed$),
        tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        tap(() => Log.info('Deleted associated organization: ', this, associatedOrganizationId)),
      ).subscribe();
  }
}
