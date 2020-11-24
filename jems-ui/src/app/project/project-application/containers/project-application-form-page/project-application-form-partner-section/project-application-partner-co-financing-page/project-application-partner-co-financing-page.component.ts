import {ChangeDetectionStrategy, Component} from '@angular/core';
import {ProjectPartnerStore} from '../../services/project-partner-store.service';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {
  ProjectPartnerCoFinancingAndContributionInputDTO,
  ProjectPartnerCoFinancingAndContributionOutputDTO,
  ProgrammeFundOutputDTO,
  OutputCall,
  ProjectPartnerBudgetService,
  CallService,
} from '@cat/api';
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

  saveError$ = new Subject<HttpErrorResponse | null>();
  saveSuccess$ = new Subject<boolean>();
  saveFinances$ = new Subject<ProjectPartnerCoFinancingAndContributionInputDTO>();
  cancelEdit$ = new Subject<void>();

  private initialCoFinancing$: Observable<ProjectPartnerCoFinancingAndContributionOutputDTO> = this.partnerStore.partner$
    .pipe(
      filter(partner => !!partner.id),
      map(partner => partner.financing),
    );
  private saveCoFinancing$: Observable<ProjectPartnerCoFinancingAndContributionOutputDTO> = this.saveFinances$
    .pipe(
      withLatestFrom(this.partnerStore.partner$),
      mergeMap(([finances, partner]) =>
        this.projectPartnerBudgetService.updateProjectPartnerCoFinancing(partner.id, finances)
      ),
      tap(() => this.saveSuccess$.next(true)),
      tap(() => this.saveError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.saveError$.next(error);
        throw error;
      })
    );

  private amountChanged$: Observable<number> = this.partnerStore.totalAmountChanged$
    .pipe(
      withLatestFrom(this.partnerStore.partner$),
      mergeMap(([, partner]) => this.projectPartnerBudgetService.getTotal(partner.id)),
    );

  private initialAmount$: Observable<number> = this.partnerStore.partner$
    .pipe(
      filter(partner => !!partner.id),
      mergeMap(partner => this.projectPartnerBudgetService.getTotal(partner.id)),
    );

  private callFunds$: Observable<ProgrammeFundOutputDTO[]> = this.projectStore.getProject()
    .pipe(
      map(project => project.call.id),
      mergeMap(callId => this.callService.getCallById(callId)),
      map((call: OutputCall) => call.funds),
    );

  details$ = combineLatest([
    merge(this.initialCoFinancing$, this.saveCoFinancing$),
    merge(this.initialAmount$, this.amountChanged$),
    this.callFunds$,
    this.cancelEdit$.pipe(startWith(null))
  ])
    .pipe(
      map(([finances, amount, funds]) => ({
        financingAndContribution: finances,
        callFunds: funds,
        totalAmount: Numbers.truncateNumber(amount),
      }))
    );

  constructor(public partnerStore: ProjectPartnerStore,
              private callService: CallService,
              public projectStore: ProjectStore,
              private activatedRoute: ActivatedRoute,
              private projectPartnerBudgetService: ProjectPartnerBudgetService) {
  }

}
