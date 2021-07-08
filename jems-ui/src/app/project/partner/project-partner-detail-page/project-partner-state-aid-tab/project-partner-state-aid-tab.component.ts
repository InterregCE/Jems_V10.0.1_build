import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectPartnerDetailPageStore} from '@project/partner/project-partner-detail-page/project-partner-detail-page.store';
import {ActivatedRoute} from '@angular/router';
import {catchError, map, take, tap} from 'rxjs/operators';
import {ProjectPartnerStateAidDTO} from '@cat/api';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-project-partner-state-aid-tab',
  templateUrl: './project-partner-state-aid-tab.component.html',
  styleUrls: ['./project-partner-state-aid-tab.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectPartnerStateAidTabComponent {
  partnerId = this.activatedRoute?.snapshot?.params?.partnerId;

  data$: Observable<{
    stateAid: ProjectPartnerStateAidDTO
  }>;

  form = this.formBuilder.group({
    answer1: [],
    justification1: [],
    answer2: [],
    justification2: [],
    answer3: [],
    justification3: [],
    answer4: [],
    justification4: []
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private activatedRoute: ActivatedRoute,
              private pageStore: ProjectPartnerDetailPageStore) {
    this.formService.init(this.form, this.pageStore.isProjectEditable$);
    this.data$ = this.pageStore.stateAid$
      .pipe(
        tap(stateAid => this.resetForm(stateAid)),
        map(stateAid => ({stateAid}))
      );
  }

  updateStateAid(): void {
    this.pageStore.updateStateAid(this.partnerId, this.form.value)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.partner.state.aid..saved')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  resetForm(stateAid?: ProjectPartnerStateAidDTO): void {
    this.form.reset(stateAid);
  }
}
