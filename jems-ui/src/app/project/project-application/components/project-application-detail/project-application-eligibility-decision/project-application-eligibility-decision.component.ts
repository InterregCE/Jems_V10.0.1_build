import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {Forms} from '../../../../../common/utils/forms';
import {take, takeUntil} from 'rxjs/internal/operators';
import {MatDialog} from '@angular/material/dialog';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {InputProjectStatus, OutputProject, OutputProjectStatus} from '@cat/api';
import {filter} from 'rxjs/operators';

@Component({
  selector: 'app-project-eligibility-decision',
  templateUrl: './project-application-eligibility-decision.component.html',
  styleUrls: ['./project-application-eligibility-decision.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationEligibilityDecisionComponent extends AbstractForm implements OnInit {
  @Input()
  project: OutputProject;

  @Output()
  changeStatus = new EventEmitter<InputProjectStatus>();
  @Output()
  cancel = new EventEmitter<void>();

  ELIGIBLE = 'ELIGIBLE';
  INELIGIBLE = 'INELIGIBLE';
  options: string[] = [this.ELIGIBLE, this.INELIGIBLE];
  projectId = this.activatedRoute.snapshot.params.projectId;

  today = new Date();
  dateErrors = {
    required: 'project.decision.date.unknown',
    matDatepickerMax: 'project.decision.date.must.be.in.the.past',
    matDatepickerParse: 'common.date.should.be.valid'
  };

  notesForm = this.formBuilder.group({
    assessment: ['', Validators.required],
    notes: ['', Validators.maxLength(1000)],
    decisionDate: ['', Validators.required]
  });

  notesErrors = {
    maxlength: 'eligibility.decision.notes.size.too.long',
  };

  selectedAssessment: string;


  constructor(
    private dialog: MatDialog,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setEligibilityDecisionValue();
    this.notesForm.controls.notes.setValue(this.project.eligibilityDecision?.note);
    this.notesForm.controls.decisionDate.setValue(this.project.eligibilityDecision?.decisionDate);
  }

  getForm(): FormGroup | null {
    return null;
  }

  onSubmit(): void {
    this.confirmEligibilityDecision();
  }

  onCancel(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId]);
  }

  assessmentChangeHandler(event: any): void {
    this.selectedAssessment = event.value;
  }

  private confirmEligibilityDecision(): void {
    console.log(this.selectedAssessment);
    Forms.confirmDialog(
      this.dialog,
      'project.assessment.eligibilityDecision.dialog.title',
      this.getEligibilityDecisionMessage()
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() => {
      this.changeStatus.emit({
        status: this.getEligibilityDecisionValue(),
        note: this.notesForm?.controls?.notes?.value,
        date: this.notesForm?.controls?.decisionDate?.value.format('YYYY-MM-DD')
      });
    });
  }

  private getEligibilityDecisionValue(): OutputProjectStatus.StatusEnum {
    return this.selectedAssessment === this.INELIGIBLE
      ? OutputProjectStatus.StatusEnum.INELIGIBLE
      : OutputProjectStatus.StatusEnum.ELIGIBLE;
  }

  private setEligibilityDecisionValue(): void {
    if (this.project?.eligibilityDecision) {
      if (this.project?.eligibilityDecision?.status === OutputProjectStatus.StatusEnum.INELIGIBLE) {
        this.notesForm.controls.assessment.setValue(this.INELIGIBLE);
      } else {
        this.notesForm.controls.assessment.setValue(this.ELIGIBLE);
      }
    }
  }

  private getEligibilityDecisionMessage(): string {
    if (this.selectedAssessment === this.ELIGIBLE) {
      return 'project.assessment.eligibilityDecision.dialog.message.eligible';
    }
    return 'project.assessment.eligibilityDecision.dialog.message.ineligible';
  }
}
