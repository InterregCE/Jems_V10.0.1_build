import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {
  InputProgrammePriorityCreate,
  InputProgrammePriorityPolicy,
  OutputProgrammePriority,
  ProgrammePriorityService
} from '@cat/api';
import {filter, take, takeUntil, tap} from 'rxjs/operators';
import {Log} from '../../../../common/utils/log';
import {Forms} from '../../../../common/utils/forms';

@Component({
  selector: 'app-programme-priority-submission',
  templateUrl: './programme-priority-submission.component.html',
  styleUrls: ['./programme-priority-submission.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammePrioritySubmissionComponent extends AbstractForm implements OnInit {
  OutputProgrammePriority = OutputProgrammePriority;

  objectives: string[] = [];
  objectivesWithPolicies: { [key: string]: string[] } = {};
  currentObjective: string;
  policies: FormGroup = this.formBuilder.group({});

  currentPolicies: InputProgrammePriorityPolicy[] = [];

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
              private programmePriorityService: ProgrammePriorityService,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.programmePriorityService.getFreePrioritiesWithPolicies()
      .pipe(
        takeUntil(this.destroyed$),
        tap(freePrioritiesWithPolicies => Log.info('Fetched free programme priorities with policies:', this, freePrioritiesWithPolicies)),
      ).subscribe(freePrioritiesWithPolicies => {
      this.objectives = Object.keys(freePrioritiesWithPolicies);
      this.objectivesWithPolicies = freePrioritiesWithPolicies;
    });
  }

  getForm(): FormGroup | null {
    return this.priorityForm;
  }

  onSubmit(): void {
    Forms.confirmDialog(
      this.dialog,
      'programme.data.dialog.title',
      'programme.data.dialog.message'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() => {
      this.programmePriorityService.create({
        code: this.priorityForm.controls.priorityCode.value,
        title: this.priorityForm.controls.priorityTitle.value,
        objective: this.currentObjective,
        programmePriorityPolicies: this.currentPolicies,
      } as InputProgrammePriorityCreate).subscribe();
    });
  }

  changeCurrentObjective(objective: string) {
    this.currentObjective = objective;
    const policies = this.objectivesWithPolicies[objective]
      .reduce((map, obj) => {
        map[obj] = Validators.maxLength(50);
        return map;
      }, {});
    this.policies = Forms.toFormGroup(policies)
  }

  changeCurrentPolicies(event: InputProgrammePriorityPolicy[]) {
    this.currentPolicies = event;
  }
}
