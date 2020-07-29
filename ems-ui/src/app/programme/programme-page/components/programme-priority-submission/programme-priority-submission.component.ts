import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormBuilder, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {
  InputProgrammePriorityCreate,
  InputProgrammePriorityPolicy
} from '@cat/api';
import {filter, take, takeUntil} from 'rxjs/operators';
import {Forms} from '../../../../common/utils/forms';

@Component({
  selector: 'app-programme-priority-submission',
  templateUrl: './programme-priority-submission.component.html',
  styleUrls: ['./programme-priority-submission.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePrioritySubmissionComponent extends AbstractForm implements OnInit {

  @Input()
  objectives: string[];
  @Input()
  objectivesWithPolicies: { [key: string]: string[] };
  @Output()
  savePriority: EventEmitter<InputProgrammePriorityCreate> = new EventEmitter<InputProgrammePriorityCreate>();
  @Output()
  cancelPriority: EventEmitter<number> = new EventEmitter<number>();

  currentObjective: string;
  policyForm: FormGroup = this.formBuilder.group({});
  checked = new Map<string, boolean>();

  priorityForm = this.formBuilder.group({
    priorityCode: ['', Validators.maxLength(50)],
    priorityTitle: ['', Validators.maxLength(300)],
    policyObjective: ['']
  });

  priorityCodeErrors = {
    maxlength: 'programme.priority.code.size.too.long',
  };

  priorityTitleErrors = {
    maxlength: 'programme.priority.title.size.too.long',
  };

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  getForm(): FormGroup | null {
    return this.priorityForm;
  }

  onCancel(): void {
    this.cancelPriority.emit(1);
  }

  onSubmit(): void {
    Forms.confirmDialog(
      this.dialog,
      'programme.priority.dialog.title',
      'programme.priority.dialog.message'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() => {
      this.submitPriority();
    });
  }

  changeCurrentObjective(objective: string) {
    this.currentObjective = objective;
    const policies = new Map<string, ValidatorFn[]>();
    this.objectivesWithPolicies[objective].forEach((obj => {
      policies.set(obj, [Validators.maxLength(50)]);
    }))
    this.policyForm = Forms.toFormGroup(policies)
  }

  submitPriority(): void {
    const priority = {
      code: this.priorityForm.controls.priorityCode.value,
      title: this.priorityForm.controls.priorityTitle.value,
      objective: this.currentObjective,
      programmePriorityPolicies: this.buildPolicyList()
    } as InputProgrammePriorityCreate;
    this.savePriority.emit(priority);
  }

  buildPolicyList(): InputProgrammePriorityPolicy[] {
    const currentlySelected: InputProgrammePriorityPolicy[] = []
    this.checked.forEach((value, key) => {
      if (value) {
        currentlySelected.push({
          code: this.policyForm.controls[key].value,
          programmeObjectivePolicy: key,
        } as InputProgrammePriorityPolicy);
      }
    })
    return currentlySelected;
  }
}
