import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectPartnerStore} from '../../services/project-partner-store.service';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  InputProjectPartnerCoFinancingWrapper,
  OutputProjectPartnerCoFinancing,
  OutputProgrammeFund,
  ProjectPartnerService,
  ProjectPartnerBudgetService,
  CallService,
} from '@cat/api';
import {I18nValidationError} from '@common/validation/i18n-validation-error';
import {catchError, filter, map, mergeMap, startWith, tap, withLatestFrom} from 'rxjs/operators';
import {ActivatedRoute} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {ProjectStore} from '../../../project-application-detail/services/project-store.service';
import {Numbers} from '../../../../../../common/utils/numbers';

@Component({
  selector: 'app-project-application-partner-co-financing-page',
  templateUrl: './project-application-partner-co-financing-page.component.html',
  styleUrls: ['./project-application-partner-co-financing-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationPartnerCoFinancingPageComponent {

  projectId = this.activatedRoute?.snapshot?.params?.projectId;

  saveError$ = new Subject<I18nValidationError | null>();
  saveSuccess$ = new Subject<boolean>();
  saveFinances$ = new Subject<InputProjectPartnerCoFinancingWrapper>();
  cancelEdit$ = new Subject<void>();

  private initialCoFinancing$: Observable<OutputProjectPartnerCoFinancing[]> = this.partnerStore.getProjectPartner()
    .pipe(
      filter(partner => !!partner.id),
      map(partner => partner.financing),
    );
  private saveCoFinancing$: Observable<OutputProjectPartnerCoFinancing[]> = this.saveFinances$
    .pipe(
      withLatestFrom(this.partnerStore.getProjectPartner()),
      mergeMap(([finances, partner]) =>
        this.projectPartnerService.updateProjectPartnerCoFinancing(partner.id, this.projectId, finances)
      ),
      map(partner => partner.financing),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error.error);
        throw error;
      })
    );

  private amountChanged$ = this.partnerStore.totalAmountChanged$
    .pipe(
      withLatestFrom(this.partnerStore.getProjectPartner()),
      mergeMap(([, partner]) => this.projectPartnerBudgetService.getTotal(partner.id)),
    );

  private initialAmount$: Observable<number> = this.partnerStore.getProjectPartner()
    .pipe(
      filter(partner => !!partner.id),
      mergeMap(partner => this.projectPartnerBudgetService.getTotal(partner.id)),
    );

  private callFunds$: Observable<OutputProgrammeFund[]> = this.projectStore.getProject()
    .pipe(
      map(project => project.call.id),
      mergeMap(callId => this.callService.getCallById(callId)),
      map(call => call.funds),
    );

  details$ = combineLatest([
    merge(this.initialCoFinancing$, this.saveCoFinancing$),
    merge(this.initialAmount$, this.amountChanged$),
    this.callFunds$,
    this.cancelEdit$.pipe(startWith(null))
  ])
    .pipe(
      map(([finances, amount, funds]) => ({
        finances,
        callFunds: funds,
        totalAmount: Numbers.truncateNumber(amount),
      }))
    );

  constructor(public partnerStore: ProjectPartnerStore,
              private callService: CallService,
              public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectPartnerBudgetService: ProjectPartnerBudgetService,
              private projectPartnerService: ProjectPartnerService) {
  }

}
