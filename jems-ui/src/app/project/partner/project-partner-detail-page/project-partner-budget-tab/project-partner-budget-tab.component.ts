import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectStore} from '../../../project-application/containers/project-application-detail/services/project-store.service';
import {ProjectPartnerBudgetTabService} from './project-partner-budget-tab.service';
import {FormService} from '@common/components/section/form/form.service';
import {RoutingService} from '@common/services/routing.service';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {ActivatedRoute} from '@angular/router';
import {filter, map, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Observable} from 'rxjs';
import {ProjectCallSettingsDTO} from '@cat/api';
import CallTypeEnum = ProjectCallSettingsDTO.CallTypeEnum;

@UntilDestroy()
@Component({
  selector: 'jems-project-partner-budget-tab',
  templateUrl: './project-partner-budget-tab.component.html',
  styleUrls: ['./project-partner-budget-tab.component.scss'],
  providers: [ProjectPartnerBudgetTabService, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerBudgetTabComponent {

  isCallSpf$: Observable<boolean>;
  constructor(public projectStore: ProjectStore, private activatedRoute: ActivatedRoute, private router: RoutingService, private visibilityStatusService: FormVisibilityStatusService) {
    this.isCallSpf$ = projectStore.projectCallType$.pipe(map((type) => type === CallTypeEnum.SPF));
    visibilityStatusService.isVisible$((APPLICATION_FORM.SECTION_B.BUDGET_AND_CO_FINANCING)).pipe(
      untilDestroyed(this),
      filter(isVisible => !isVisible),
      tap(() => this.router.navigate(['../identity'], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'})),
    ).subscribe();
  }
}
