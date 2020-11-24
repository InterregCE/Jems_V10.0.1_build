import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {distinctUntilChanged, map, takeUntil, tap} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {ProjectPartnerService} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {ProjectStore} from '../../project-application/containers/project-application-detail/services/project-store.service';
import {TabService} from '../../../common/services/tab.service';
import {ProjectPartnerStore} from '../../project-application/containers/project-application-form-page/services/project-partner-store.service';

@Component({
  templateUrl: './project-partner-detail-page.component.html',
  styleUrls: ['./project-partner-detail-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerDetailPageComponent extends BaseComponent implements OnInit, OnDestroy {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  partnerId = this.activatedRoute?.snapshot?.params?.partnerId;

  activeTab$ = this.tabService.currentTab(
    ProjectPartnerDetailPageComponent.name + this.partnerId
  );

  constructor(private partnerService: ProjectPartnerService,
              private activatedRoute: ActivatedRoute,
              public projectStore: ProjectStore,
              public partnerStore: ProjectPartnerStore,
              private router: Router,
              private tabService: TabService) {
    super();
    this.activatedRoute.params.pipe(
      takeUntil(this.destroyed$),
      map(params => params.partnerId),
      distinctUntilChanged(),
      tap(id => this.partnerStore.init(id, this.projectId)),
    ).subscribe();
  }

  ngOnInit(): void {
    this.projectStore.init(this.projectId);
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
    this.tabService.cleanupTab(ProjectPartnerDetailPageComponent.name + this.partnerId);
  }

  changeTab(tabIndex: number): void {
    this.tabService.changeTab(ProjectPartnerDetailPageComponent.name + this.partnerId, tabIndex);
  }

}
