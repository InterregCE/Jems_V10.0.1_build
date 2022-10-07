import {UntilDestroy} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {ContractPartnerStore} from '@project/project-application/contract-partner/contract-partner.store';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {ProjectPartnerSummaryDTO} from '@cat/api';
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

  projectId = this.activatedRoute.snapshot.params.projectId;
  partnerId = this.activatedRoute.snapshot.params.partnerId;
  data$: Observable<{
    partnerSummary: ProjectPartnerSummaryDTO;
  }>;
  Alert = Alert;

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.contractPartnerStore.partnerSummary$,
    ])
      .pipe(
        map(([partnerSummary]) => ({partnerSummary}))
      );
  }

  constructor(private projectSidenavService: ProjectApplicationFormSidenavService,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute,
              private contractPartnerStore: ContractPartnerStore,
              private adaptTranslationKeyByCallTypePipe: AdaptTranslationKeyByCallTypePipe,
              private customTranslatePipe: CustomTranslatePipe) {
  }

  getPartnerTranslationString(partner: ProjectPartnerSummaryDTO): Observable<string> {
    return this.adaptTranslationKeyByCallTypePipe.transform('common.label.project.partner.role.shortcut.' + partner.role)
      .pipe(map(data => this.customTranslatePipe.transform(data, {partner: partner.sortNumber}) + ` ${partner.abbreviation}`));
  }
}
