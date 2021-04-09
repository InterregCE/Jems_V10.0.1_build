import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {ApplicationActionInfoDTO, ProjectDetailDTO, ProjectStatusDTO} from '@cat/api';
import {FormBuilder, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '../../../../common/utils/forms';
import {filter, switchMap, take, tap} from 'rxjs/operators';
import {ProjectFundingDecisionStore} from '../project-funding-decision-store.service';
import {RoutingService} from '../../../../common/services/routing.service';
import {ProjectApplicationFormSidenavService} from '../../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';

@Component({
  selector: 'app-project-application-funding-decision',
  templateUrl: './project-application-funding-decision.component.html',
  styleUrls: ['./project-application-funding-decision.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFundingDecisionComponent implements OnInit {

  @Input()
  project: ProjectDetailDTO;
  @Input()
  status: ProjectStatusDTO;
  @Input()
  options: ProjectStatusDTO.StatusEnum[];
  @Input()
  submitLabel: string;

  today = new Date();
  dateErrors = {
    required: 'common.error.date.required',
    matDatepickerMax: 'project.decision.date.must.be.in.the.past',
    matDatepickerMin: 'project.decision.date.must.be.after.eligibility.date',
    matDatepickerParse: 'common.date.should.be.valid'
  };
  noteErrors = {
    maxlength: 'project.decision.notes.too.long'
  };

  decisionForm = this.formBuilder.group({
    status: ['', Validators.required],
    notes: ['', Validators.maxLength(10000)],
    decisionDate: ['', Validators.required]
  });

  constructor(private dialog: MatDialog,
              private fundingDecisionStore: ProjectFundingDecisionStore,
              private formBuilder: FormBuilder,
              private router: RoutingService,
              private sidenavService: ProjectApplicationFormSidenavService) {
  }

  ngOnInit(): void {
    this.decisionForm.controls.status.setValue(this.status?.status);
    this.decisionForm.controls.notes.setValue(this.status?.note);
    this.decisionForm.controls.decisionDate.setValue(this.status?.decisionDate);
  }

  onSubmit(): void {
    const statusInfo: ApplicationActionInfoDTO = {
      note: this.decisionForm?.controls?.notes?.value,
      date: this.decisionForm?.controls?.decisionDate?.value?.format('YYYY-MM-DD')
    };
    Forms.confirmDialog(
      this.dialog,
      'project.assessment.fundingDecision.dialog.title',
      'project.assessment.fundingDecision.dialog.message.' + this.decisionForm?.controls?.status?.value
    ).pipe(
      take(1),
      filter(yes => !!yes),
      switchMap(() => {
        if (this.decisionForm?.controls?.status?.value === ProjectStatusDTO.StatusEnum.APPROVED) {
          return this.fundingDecisionStore.approveApplication(this.project.id, statusInfo);
        } else if (this.decisionForm?.controls?.status?.value === ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS) {
          return this.fundingDecisionStore.approveApplicationWithCondition(this.project.id, statusInfo);
        } else {
          return this.fundingDecisionStore.refuseApplication(this.project.id, statusInfo);
        }
      }),
      tap(() => this.redirectToProject())
    ).subscribe();
  }

  redirectToProject(): void {
    this.router.navigate(['app', 'project', 'detail', this.project.id]);
  }
}
