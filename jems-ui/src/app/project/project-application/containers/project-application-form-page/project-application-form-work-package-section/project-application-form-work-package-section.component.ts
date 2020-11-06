import {ChangeDetectionStrategy, Component} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {mergeMap, map, startWith, take, tap} from 'rxjs/operators';
import {Tables} from '../../../../../common/utils/tables';
import {Log} from '../../../../../common/utils/log';
import {WorkPackageService} from '@cat/api';
import {Permission} from '../../../../../security/permissions/permission';
import {ProjectApplicationFormSidenavService} from '../services/project-application-form-sidenav.service';
import {ActivatedRoute} from '@angular/router';
import {ProjectStore} from '../../project-application-detail/services/project-store.service';

@Component({
  selector: 'app-project-application-form-work-package-section',
  templateUrl: './project-application-form-work-package-section.component.html',
  styleUrls: ['./project-application-form-work-package-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormWorkPackageSectionComponent {
  Permission = Permission;
  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  currentWorkPackagePage$ =
    combineLatest([
      this.newPageIndex$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
      this.newPageSize$.pipe(startWith(Tables.DEFAULT_INITIAL_PAGE_SIZE)),
      this.newSort$.pipe(
        startWith({active: 'id', direction: 'asc'}),
        map(sort => sort?.direction ? sort : {active: 'id', direction: 'asc'}),
        map(sort => `${sort.active},${sort.direction}`)
      )
    ])
      .pipe(
        mergeMap(([pageIndex, pageSize, sort]) =>
          this.workPackageService.getWorkPackagesByProjectId(this.projectId, pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the work packages:', this, page.content)),
      );

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private workPackageService: WorkPackageService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
    this.projectStore.init(this.projectId);
  }

  deleteWorkPackage(workPackageId: number): void {
    this.workPackageService.deleteWorkPackage(workPackageId, this.projectId)
      .pipe(
        take(1),
        tap(() => this.newPageIndex$.next(Tables.DEFAULT_INITIAL_PAGE_INDEX)),
        tap(() => Log.info('Deleted work package: ', this, workPackageId)),
        tap(() => this.projectApplicationFormSidenavService.refreshPackages(this.projectId))
      ).subscribe();
  }

}
