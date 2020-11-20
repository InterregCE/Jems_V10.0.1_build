import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {ActivatedRoute, Router} from '@angular/router';
import {WorkPackageService} from '@cat/api';
import {distinctUntilChanged, map, takeUntil, tap} from 'rxjs/operators';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';
import {TabService} from '../../../../../../common/services/tab.service';
import {ProjectWorkpackageStoreService} from '../../services/project-workpackage-store.service';

@Component({
  selector: 'app-work-package-details',
  templateUrl: './work-package-details.component.html',
  styleUrls: ['./work-package-details.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorkPackageDetailsComponent extends BaseComponent implements OnInit, OnDestroy {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  workPackageId = this.activatedRoute?.snapshot?.params?.workPackageId;

  activeTab$ = this.tabService.currentTab(
    WorkPackageDetailsComponent.name + this.workPackageId
  );

  constructor(private workPackageService: WorkPackageService,
              private activatedRoute: ActivatedRoute,
              public projectStore: ProjectStore,
              public workPackageStore: ProjectWorkpackageStoreService,
              private router: Router,
              private tabService: TabService) {
    super();
    this.activatedRoute.params.pipe(
      takeUntil(this.destroyed$),
      map(params => params.workPackageId),
      distinctUntilChanged(),
      tap(id => this.workPackageStore.init(id, this.projectId)),
    ).subscribe();
  }

  ngOnInit(): void {
    this.projectStore.init(this.projectId);
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
    this.tabService.cleanupTab(WorkPackageDetailsComponent.name + this.workPackageId);
  }

  changeTab(tabIndex: number): void {
    this.tabService.changeTab(WorkPackageDetailsComponent.name + this.workPackageId, tabIndex);
  }
}
