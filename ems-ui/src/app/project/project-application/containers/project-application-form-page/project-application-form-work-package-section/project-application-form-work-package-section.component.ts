import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {combineLatest, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {flatMap, map, startWith, tap} from 'rxjs/operators';
import {Tables} from '../../../../../common/utils/tables';
import {Log} from '../../../../../common/utils/log';
import {WorkPackageService} from '@cat/api'
import {Permission} from '../../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-form-work-package-section',
  templateUrl: './project-application-form-work-package-section.component.html',
  styleUrls: ['./project-application-form-work-package-section.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormWorkPackageSectionComponent{
  Permission = Permission;

  @Input()
  projectId: number;
  @Input()
  editable: boolean;

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
        flatMap(([pageIndex, pageSize, sort]) =>
          this.workPackageService.getWorkPackagesByProjectId(this.projectId, pageIndex, pageSize, sort)),
        tap(page => Log.info('Fetched the work packages:', this, page.content)),
      );


  constructor(private workPackageService: WorkPackageService) { }

}
