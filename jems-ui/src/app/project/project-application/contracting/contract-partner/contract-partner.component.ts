import {UntilDestroy} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {ContractPartnerStore} from '@project/project-application/contracting/contract-partner/contract-partner.store';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {ContractingPartnerStateAidDeMinimisSectionDTO, ContractingPartnerStateAidGberSectionDTO, ProjectPartnerSummaryDTO} from '@cat/api';
import {AdaptTranslationKeyByCallTypePipe} from '@common/pipe/adapt-translation-by-call-type.pipe';
import {CustomTranslatePipe} from '@common/pipe/custom-translate-pipe';
import {Alert} from '@common/components/forms/alert';

@UntilDestroy()
@Component({
  selector: 'jems-contract-partner',
  templateUrl: './contract-partner.component.html',
  styleUrls: ['./contract-partner.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class ContractPartnerComponent implements OnInit {

  data$: Observable<{
    partnerSummary: ProjectPartnerSummaryDTO;
    canView: boolean;
    deMinimis: ContractingPartnerStateAidDeMinimisSectionDTO | null;
    GBER: ContractingPartnerStateAidGberSectionDTO | null;
  }>;
  Alert = Alert;

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.contractPartnerStore.partnerSummary$,
      this.contractPartnerStore.userCanViewContractPartner$,
      this.contractPartnerStore.deMinimis$,
      this.contractPartnerStore.GBER$,
    ])
      .pipe(
        map(([partnerSummary, canView, deMinimis, GBER]) => ({partnerSummary, canView, deMinimis, GBER})),
      );
  }

  constructor(public contractPartnerStore: ContractPartnerStore,
              private adaptTranslationKeyByCallTypePipe: AdaptTranslationKeyByCallTypePipe,
              private customTranslatePipe: CustomTranslatePipe) {
  }

  getPartnerTranslationString(partner: ProjectPartnerSummaryDTO): Observable<string> {
    return this.adaptTranslationKeyByCallTypePipe.transform('common.label.project.partner.role.shortcut.' + partner.role)
      .pipe(map(data => this.customTranslatePipe.transform(data, {partner: partner.sortNumber}) + ` ${partner.abbreviation}`));
  }

}
