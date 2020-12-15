import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {BaseComponent} from '@common/components/base-component';
import {ActivatedRoute} from '@angular/router';
import {WorkPackageService} from '@cat/api';
import {distinctUntilChanged, map, takeUntil, tap} from 'rxjs/operators';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {TabService} from '../../../common/services/tab.service';
import {ProjectWorkPackagePageStore} from './project-work-package-page-store.service';

@Component({
  selector: 'app-project-work-package-detail-page',
  templateUrl: './project-work-package-detail-page.component.html',
  styleUrls: ['./project-work-package-detail-page.component.scss'],
  providers: [ProjectWorkPackagePageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectWorkPackageDetailPageComponent extends BaseComponent implements OnInit, OnDestroy {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  workPackageId = this.activatedRoute?.snapshot?.params?.workPackageId;

  activeTab$ = this.tabService.currentTab(
    ProjectWorkPackageDetailPageComponent.name + this.workPackageId
  );

  constructor(private workPackageService: WorkPackageService,
              private activatedRoute: ActivatedRoute,
              public projectStore: ProjectStore,
              public workPackageStore: ProjectWorkPackagePageStore,
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
    this.tabService.cleanupTab(ProjectWorkPackageDetailPageComponent.name + this.workPackageId);
  }

  changeTab(tabIndex: number): void {
    this.tabService.changeTab(ProjectWorkPackageDetailPageComponent.name + this.workPackageId, tabIndex);
  }
}
