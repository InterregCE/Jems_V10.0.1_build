import {UntilDestroy} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {ContractPartnerStore} from '@project/project-application/contracting/contract-partner/contract-partner.store';
import {combineLatest, Observable, Subject} from 'rxjs';
import {catchError, map, take, tap} from 'rxjs/operators';
import {
    ContractingPartnerStateAidDeMinimisSectionDTO,
    ContractingPartnerStateAidGberSectionDTO,
    ProjectPartnerSummaryDTO
} from '@cat/api';
import {AdaptTranslationKeyByCallTypePipe} from '@common/pipe/adapt-translation-by-call-type.pipe';
import {CustomTranslatePipe} from '@common/pipe/custom-translate-pipe';
import {Alert} from '@common/components/forms/alert';
import {APIError} from '@common/models/APIError';

@UntilDestroy()
@Component({
  selector: 'jems-contract-partner',
  templateUrl: './contract-partner.component.html',
  styleUrls: ['./contract-partner.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class ContractPartnerComponent implements OnInit {

  projectId = this.activatedRoute.snapshot.params.projectId;
  partnerId = this.activatedRoute.snapshot.params.partnerId;
  data$: Observable<{
    partnerSummary: ProjectPartnerSummaryDTO;
    canView: boolean;
    deMinimis: ContractingPartnerStateAidDeMinimisSectionDTO | null;
    GBER: ContractingPartnerStateAidGberSectionDTO | null;
  }>;
  Alert = Alert;

  deMinimisError$= new Subject<APIError | null>();
  gberError$ = new Subject<APIError | null>();
  deMinimisSuccess$ = new Subject<any>();
  gberSuccess$ = new Subject<any>();

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.contractPartnerStore.partnerSummary$,
      this.contractPartnerStore.userCanViewContractPartner$,
      this.contractPartnerStore.deMinimis$,
      this.contractPartnerStore.GBER$,
    ])
      .pipe(
        map(([partnerSummary, canView, deMinimis, GBER]) => ({partnerSummary, canView, deMinimis, GBER}))
      );
  }

  constructor(private projectSidenavService: ProjectApplicationFormSidenavService,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              public contractPartnerStore: ContractPartnerStore,
              private adaptTranslationKeyByCallTypePipe: AdaptTranslationKeyByCallTypePipe,
              private customTranslatePipe: CustomTranslatePipe) {
  }

  getPartnerTranslationString(partner: ProjectPartnerSummaryDTO): Observable<string> {
    return this.adaptTranslationKeyByCallTypePipe.transform('common.label.project.partner.role.shortcut.' + partner.role)
      .pipe(map(data => this.customTranslatePipe.transform(data, {partner: partner.sortNumber}) + ` ${partner.abbreviation}`));
  }

  handleUpdateDeMinimis($event: any) {
      this.contractPartnerStore.updateDeMinimis($event).pipe(
          take(1),
          tap(() => this.deMinimisSuccess$.next('project.application.contract.monitoring.project.de.minimis.saved')),
          catchError(async (error) => this.deMinimisError$.next(error)),
      ).subscribe();
  }

    handleUpdateGber($event: any) {
        this.contractPartnerStore.updateGber($event).pipe(
            take(1),
            tap(() => this.gberSuccess$.next('project.application.contract.partner.section.gber.saved')),
            catchError(async (error) => this.gberError$.next(error)),
        ).subscribe();
    }
}
