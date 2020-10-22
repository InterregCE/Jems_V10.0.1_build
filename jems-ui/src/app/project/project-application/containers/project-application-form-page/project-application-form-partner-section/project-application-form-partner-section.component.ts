import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {mergeMap, map, startWith, take, tap} from 'rxjs/operators';
import {Tables} from '../../../../../common/utils/tables';
import {Log} from '../../../../../common/utils/log';
import {ProjectPartnerService, ProjectAssociatedOrganizationService} from '@cat/api';
import {Permission} from '../../../../../security/permissions/permission';
import {ProjectApplicationFormSidenavService} from '../services/project-application-form-sidenav.service';

@Component({
  selector: 'app-project-application-form-partner-section',
  templateUrl: './project-application-form-partner-section.component.html',
  styleUrls: ['./project-application-form-partner-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerSectionComponent {
  Permission = Permission;

  @Input()
  projectId: number;
  @Input()
  editable: boolean;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  newPageSizeAO$ = new Subject<number>();
  newPageIndexAO$ = new Subject<number>();
  newSortAO$ = new Subject<Partial<MatSort>>();

  partnerPage$ =
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
          // put lead partner on top by default
          this.projectPartnerService.getProjectPartners(this.projectId, pageIndex, pageSize, ['role,asc', sort])),
        tap(page => Log.info('Fetched the project partners:', this, page.content)),
      );

  associatedOrganizationsPage$ =
    combineLatest([
      this.newPageIndexAO$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSizeAO$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSortAO$.pipe(
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

  constructor(private projectPartnerService: ProjectPartnerService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private projectAssociatedOrganizationService: ProjectAssociatedOrganizationService) {
  }

  deletePartner(partnerId: number): void {
    this.projectPartnerService.deleteProjectPartner(partnerId, this.projectId)
      .pipe(
        take(1),
        tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        tap(() => this.newPageIndexAO$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)), // deletion of partner can result to deletion of AO as well
        tap(() => Log.info('Deleted partner: ', this, partnerId)),
        tap(() => this.projectApplicationFormSidenavService.refreshPartners()),
        tap(() => this.projectApplicationFormSidenavService.refreshOrganizations()),
      ).subscribe();
  }

  deleteAssociatedOrganization(associatedOrganizationId: number): void {
    this.projectAssociatedOrganizationService.deleteAssociatedOrganization(associatedOrganizationId, this.projectId)
      .pipe(
        take(1),
        tap(() => this.newPageIndexAO$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        tap(() => Log.info('Deleted associated organization: ', this, associatedOrganizationId)),
        tap(() => this.projectApplicationFormSidenavService.refreshOrganizations()),
      ).subscribe();
  }
}
