import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {merge, of, Subject} from 'rxjs';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {HttpErrorResponse} from '@angular/common/http';
import {catchError, distinctUntilChanged, mergeMap, map, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../../../common/utils/log';
import {ActivatedRoute, Router} from '@angular/router';
import {
  InputProjectContact,
  InputProjectPartnerContribution,
  ProjectPartnerService
} from '@cat/api';
import {BaseComponent} from '@common/components/base-component';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';
import {TabService} from '../../../../../../common/services/tab.service';
import {ProjectPartnerStore} from '../../services/project-partner-store.service';

@Component({
  selector: 'app-project-application-form-partner-detail',
  templateUrl: './project-application-form-partner-detail.component.html',
  styleUrls: ['./project-application-form-partner-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerDetailComponent extends BaseComponent implements OnInit, OnDestroy {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  partnerId = this.activatedRoute?.snapshot?.params?.partnerId;

  activeTab$ = this.tabService.currentTab(
    ProjectApplicationFormPartnerDetailComponent.name + this.partnerId
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
    this.tabService.cleanupTab(ProjectApplicationFormPartnerDetailComponent.name + this.partnerId);
  }

  changeTab(tabIndex: number): void {
    this.tabService.changeTab(ProjectApplicationFormPartnerDetailComponent.name + this.partnerId, tabIndex);
  }

}
