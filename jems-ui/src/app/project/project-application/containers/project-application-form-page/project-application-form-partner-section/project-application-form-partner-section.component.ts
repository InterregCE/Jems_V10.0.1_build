import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {map, mergeMap, startWith, take, tap} from 'rxjs/operators';
import {Tables} from '@common/utils/tables';
import {Log} from '@common/utils/log';
import {ProjectBudgetPartnerSummaryDTO, ProjectPartnerService} from '@cat/api';
import {Permission} from '../../../../../security/permissions/permission';
import {ProjectApplicationFormSidenavService} from '../services/project-application-form-sidenav.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';
import {ProjectVersionStore} from '@project/common/services/project-version-store.service';
import {ProjectPartnerStore} from '@project/project-application/containers/project-application-form-page/services/project-partner-store.service';
import { Alert } from '@common/components/forms/alert';

@Component({
  selector: 'app-project-application-form-partner-section',
  templateUrl: './project-application-form-partner-section.component.html',
  styleUrls: ['./project-application-form-partner-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerSectionComponent {
  Permission = Permission;
  Alert = Alert;

  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  showSuccessMessage$ = new Subject<string | null >();

  partnerPage$ =
    combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith({active: 'sortNumber', direction: 'asc'}),
        map(sort => sort?.direction ? sort : {active: 'sortNumber', direction: 'asc'}),
        map(sort => `${sort.active},${sort.direction}`)
      ),
      this.projectVersionStore.selectedVersionParam$
    ])
      .pipe(
        mergeMap(([pageIndex, pageSize, sort, version]) =>
          // put lead partner on top by default
          this.projectPartnerService.getProjectPartners(
            this.projectId,
            pageIndex,
            pageSize,
            sort.includes('sortNumber') ? [sort] : [sort, `sortNumber,asc`],
            version)),
        tap(page => Log.info('Fetched the project partners:', this, page.content)),
      );

  constructor(public projectStore: ProjectStore,
              private projectPartnerService: ProjectPartnerService,
              private partnerStore: ProjectPartnerStore,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private projectVersionStore: ProjectVersionStore,
              private activatedRoute: ActivatedRoute) {
  }

  deletePartner(partnerId: number): void {
    this.partnerStore.deletePartner(partnerId)
      .pipe(
        take(1),
        tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        tap(() => this.projectApplicationFormSidenavService.refreshPartners(this.projectId)),
      ).subscribe();
  }

  deactivatePartner(partnerId: number, partners: ProjectBudgetPartnerSummaryDTO[]): void {
    this.partnerStore.deactivatePartner(partnerId)
      .pipe(
        take(1),
        tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        tap(() => {
          this.showSuccessMessage$.next(partners.find(it => it.partnerSummary.id === partnerId)?.partnerSummary.abbreviation);
          setTimeout(() => this.showSuccessMessage$.next(null), 4000);
        }),
        tap(() => this.projectApplicationFormSidenavService.refreshPartners(this.projectId)),
      ).subscribe();
  }
}
