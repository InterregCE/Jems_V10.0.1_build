import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {InputProjectStatus, OutputProject} from '@cat/api';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '../../../../../common/utils/forms';
import {filter, take, takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-project-application-funding-decision',
  templateUrl: './project-application-funding-decision.component.html',
  styleUrls: ['./project-application-funding-decision.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFundingDecisionComponent extends AbstractForm implements OnInit {

  @Input()
  project: OutputProject;

  @Output()
  changeStatus = new EventEmitter<InputProjectStatus>();
  @Output()
  cancel = new EventEmitter<void>();

  options = [
    InputProjectStatus.StatusEnum.APPROVED,
    InputProjectStatus.StatusEnum.APPROVEDWITHCONDITIONS,
    InputProjectStatus.StatusEnum.NOTAPPROVED
  ];

  today = new Date();
  dateErrors = {
    required: 'project.decision.date.unknown',
    matDatepickerMax: 'project.decision.date.must.be.in.the.past'
  };

  decisionForm = this.formBuilder.group({
    status: ['', Validators.required],
    notes: ['', Validators.maxLength(1000)],
    decisionDate: ['', Validators.required]
  });

  constructor(private dialog: MatDialog,
              private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.decisionForm.controls.status.setValue(this.project.fundingDecision?.status);
    this.decisionForm.controls.notes.setValue(this.project.fundingDecision?.note);
    this.decisionForm.controls.decisionDate.setValue(this.project.fundingDecision?.decisionDate);
  }

  onSubmit(): void {
    Forms.confirmDialog(
      this.dialog,
      'project.assessment.fundingDecision.dialog.title',
      'project.assessment.fundingDecision.dialog.message.' + this.decisionForm?.controls?.status?.value
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() =>
      this.changeStatus.emit({
        status: this.decisionForm?.controls?.status?.value,
        note: this.decisionForm?.controls?.notes?.value,
        date: this.decisionForm?.controls?.decisionDate?.value?.format('YYYY-MM-DD')
      }));
  }

  getForm(): FormGroup | null {
    return null;
  }
}
