import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {ApplicationActionInfoDTO, ProjectDetailDTO, ProjectStatusDTO} from '@cat/api';
import {FormBuilder, Validators} from '@angular/forms';
import {tap} from 'rxjs/operators';
import {ProjectFundingDecisionStore} from '../project-funding-decision-store.service';
import {RoutingService} from '../../../../common/services/routing.service';
import {ProjectApplicationFormSidenavService} from '../../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {Observable} from 'rxjs';
import {take} from 'rxjs/internal/operators';
import {ConfirmDialogData} from '@common/components/modals/confirm-dialog/confirm-dialog.component';

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
  actionPending = false;

  dateErrors = {
    required: 'common.error.field.required',
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

  constructor(private fundingDecisionStore: ProjectFundingDecisionStore,
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
    this.getDecisionAction()
      .pipe(
        take(1),
        tap(() => this.actionPending = false),
        tap(() => this.redirectToProject())
      ).subscribe();
  }

  redirectToProject(): void {
    this.router.navigate(['app', 'project', 'detail', this.project.id]);
  }

  private getDecisionAction(): Observable<string> {
    const statusInfo: ApplicationActionInfoDTO = {
      note: this.decisionForm?.controls?.notes?.value,
      date: this.decisionForm?.controls?.decisionDate?.value?.format('YYYY-MM-DD')
    };

    if (this.decisionForm?.controls?.status?.value === ProjectStatusDTO.StatusEnum.APPROVED) {
      return this.fundingDecisionStore.approveApplication(this.project.id, statusInfo);
    } else if (this.decisionForm?.controls?.status?.value === ProjectStatusDTO.StatusEnum.APPROVEDWITHCONDITIONS) {
      return this.fundingDecisionStore.approveApplicationWithCondition(this.project.id, statusInfo);
    }
    return this.fundingDecisionStore.refuseApplication(this.project.id, statusInfo);
  }

  getFundingConfirmation(): ConfirmDialogData {
    return {
      title: 'project.assessment.fundingDecision.dialog.title',
      message: 'project.assessment.fundingDecision.dialog.message.' + this.decisionForm?.controls?.status?.value
    };
  }
}
