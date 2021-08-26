import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '../../../../common/utils/forms';
import {filter, map, startWith, take, takeUntil, tap} from 'rxjs/operators';
import {Permission} from '../../../../security/permissions/permission';
import {InputProgrammeData, OutputProgrammeData} from '@cat/api';
import {Tools} from '../../../../common/utils/tools';
import {TranslateService} from '@ngx-translate/core';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';
import {combineLatest, Observable} from 'rxjs';

@Component({
  selector: 'app-programme-data',
  templateUrl: './programme-data.component.html',
  styleUrls: ['./programme-data.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeDataComponent extends ViewEditForm implements OnInit {
  private static readonly DATE_SHOULD_BE_VALID = 'common.date.should.be.valid';
  Permission = Permission;
  tools = Tools;

  @Input()
  programme: OutputProgrammeData;
  @Output()
  saveProgrammeData: EventEmitter<InputProgrammeData> = new EventEmitter<InputProgrammeData>();

  programmeForm = this.formBuilder.group({
    cci: ['', Validators.maxLength(15)],
    title: ['', Validators.maxLength(255)],
    version: ['', Validators.maxLength(255)],
    firstYear: ['', Validators.compose([Validators.max(9999), Validators.min(1000)]),
    ],
    lastYear: ['', Validators.compose([Validators.max(9999), Validators.min(1000)])],
    eligibleFrom: [''],
    eligibleUntil: [''],
    commissionDecisionNumber: ['', Validators.maxLength(255)],
    commissionDecisionDate: [''],
    programmeAmendingDecisionNumber: ['', Validators.maxLength(255)],
    programmeAmendingDecisionDate: [''],
    projectIdProgrammeAbbreviation: ['', Validators.maxLength(12)],
    projectIdUseCallId: false,
  },                                     {
    validator: this.firstYearBeforeLastYear
  });

  projectIdExample$: Observable<string>;

  yearErrorsArgs = {
    min: {min: 1000, max: 9999},
    max: {min: 1000, max: 9999}
  };

  firstYearErrors = {
    max: 'common.error.field.number.out.of.range',
    min: 'common.error.field.number.out.of.range',
  };

  lastYearErrors = {
    max: 'common.error.field.number.out.of.range',
    min: 'common.error.field.number.out.of.range',
  };

  dateErrors = {
    matDatepickerParse: ProgrammeDataComponent.DATE_SHOULD_BE_VALID,
  };

  eligibleFromErrors = {
    matDatepickerParse: ProgrammeDataComponent.DATE_SHOULD_BE_VALID,
    matDatepickerMax: 'programme.eligibleFrom.must.be.before.eligibleUntil',
  };

  eligibleUntilErrors = {
    matDatepickerParse: ProgrammeDataComponent.DATE_SHOULD_BE_VALID,
    matDatepickerMin: 'programme.eligibleUntil.must.be.after.eligibleFrom',
  };

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService,
              public programmeEditableStateStore: ProgrammeEditableStateStore
  ) {
    super(changeDetectorRef, translationService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.projectIdExample$ = combineLatest([
      this.programmeForm.controls.projectIdProgrammeAbbreviation.valueChanges.pipe(startWith(this.programme.projectIdProgrammeAbbreviation)),
      this.programmeForm.controls.projectIdUseCallId.valueChanges.pipe(startWith(this.programme.projectIdUseCallId)),
    ]).pipe(
      map(([abbreviation, useCallId]) => `${abbreviation || ''}${useCallId ? '01' : ''}00001`),
    );
  }

  getForm(): FormGroup | null {
    return this.programmeForm;
  }

  enterViewMode(): void {
    const controls = this.programmeForm.controls;
    controls.cci.setValue(this.programme.cci);
    controls.title.setValue(this.getSizedValue(this.programme.title));
    controls.version.setValue(this.getSizedValue(this.programme.version));
    controls.firstYear.setValue(this.programme.firstYear);
    controls.lastYear.setValue(this.programme.lastYear);
    controls.eligibleFrom.setValue(this.programme.eligibleFrom);
    controls.eligibleUntil.setValue(this.programme.eligibleUntil);
    controls.commissionDecisionNumber.setValue(this.getSizedValue(this.programme.commissionDecisionNumber));
    controls.commissionDecisionDate.setValue(this.programme.commissionDecisionDate);
    controls.programmeAmendingDecisionNumber.setValue(this.getSizedValue(this.programme.programmeAmendingDecisionNumber));
    controls.programmeAmendingDecisionDate.setValue(this.programme.programmeAmendingDecisionDate);
    controls.projectIdProgrammeAbbreviation.setValue(this.programme.projectIdProgrammeAbbreviation);
    controls.projectIdUseCallId.setValue(this.programme.projectIdUseCallId);
  }

  protected enterEditMode(): void {
    const controls = this.programmeForm.controls;
    controls.title.setValue(this.programme.title);
    controls.version.setValue(this.programme.version);
    controls.commissionDecisionNumber.setValue(this.programme.commissionDecisionNumber);
    controls.programmeAmendingDecisionNumber.setValue(this.programme.programmeAmendingDecisionNumber);
  }

  private submitProgrammeData(): void {
    this.submitted = true;
    const controls = this.programmeForm?.controls;

    this.saveProgrammeData.emit({
      cci: controls?.cci?.value,
      title: controls?.title?.value,
      version: controls?.version?.value,
      firstYear: controls?.firstYear?.value,
      lastYear: controls?.lastYear?.value,
      eligibleFrom: controls?.eligibleFrom?.value,
      eligibleUntil: controls?.eligibleUntil?.value,
      commissionDecisionNumber: controls?.commissionDecisionNumber?.value,
      commissionDecisionDate: controls?.commissionDecisionDate?.value,
      programmeAmendingDecisionNumber: controls?.programmeAmendingDecisionNumber?.value,
      programmeAmendingDecisionDate: controls?.programmeAmendingDecisionDate?.value,
      projectIdProgrammeAbbreviation: controls?.projectIdProgrammeAbbreviation?.value,
      projectIdUseCallId: controls?.projectIdUseCallId?.value,
    });
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
      this.submitProgrammeData();
    });
  }

  private getSizedValue(value: string): string {
    if (value && value.length > 30) {
      return value.substring(0, 30) + '...';
    }
    return value;
  }

  firstYearBeforeLastYear(group: FormGroup): any {
    const lastYear = group.controls.lastYear;
    const firstYear = group.controls.firstYear;

    group.markAsTouched();

    if (!firstYear.value || !lastYear.value || firstYear.value <= lastYear.value) {
      return null;
    }

    return {
      lastYearBeforeFirstYear: true
    };
  }
}
