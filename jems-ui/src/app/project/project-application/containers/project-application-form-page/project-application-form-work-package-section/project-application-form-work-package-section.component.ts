import {ChangeDetectionStrategy, Component} from '@angular/core';
import {Subject} from 'rxjs';
import {startWith, switchMap, take, tap} from 'rxjs/operators';
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
  private refreshPackages$ = new Subject<void>();

  workPackages$ = this.refreshPackages$
    .pipe(
      startWith(null),
      switchMap(() => this.workPackageService.getWorkPackagesByProjectId(this.projectId)),
      tap(packages => Log.info('Fetched the work packages:', this, packages)),
    );

  constructor(public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private workPackageService: WorkPackageService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
  }

  deleteWorkPackage(workPackageId: number): void {
    this.workPackageService.deleteWorkPackage(workPackageId)
      .pipe(
        take(1),
        tap(() => this.refreshPackages$.next()),
        tap(() => Log.info('Deleted work package: ', this, workPackageId)),
        tap(() => this.projectApplicationFormSidenavService.refreshPackages(this.projectId))
      ).subscribe();
  }

}
