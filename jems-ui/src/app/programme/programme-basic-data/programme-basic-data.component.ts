import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ViewEditFormComponent} from '@common/components/forms/view-edit-form.component';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '@common/utils/forms';
import {catchError, filter, map, mergeMap, share, shareReplay, startWith, take, tap} from 'rxjs/operators';
import {OutputProgrammeData, ProgrammeDataService} from '@cat/api';
import {Tools} from '@common/utils/tools';
import {TranslateService} from '@ngx-translate/core';
import {combineLatest, merge, Observable, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {Log} from '@common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {APIError} from '@common/models/APIError';
import {Permission} from '../../security/permissions/permission';
import {ProgrammeEditableStateStore} from '../programme-page/services/programme-editable-state-store.service';
import {ProgrammePageSidenavService} from '../programme-page/services/programme-page-sidenav.service';
import {FormState} from '@common/components/forms/form-state';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-programme-basic-data',
  templateUrl: './programme-basic-data.component.html',
  styleUrls: ['./programme-basic-data.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeBasicDataComponent extends ViewEditFormComponent implements OnInit {
  private static readonly DATE_SHOULD_BE_VALID = 'common.date.should.be.valid';
  Permission = Permission;
  tools = Tools;

  programmeSaveError$ = new Subject<APIError | null>();
  programmeSaveSuccess$ = new Subject<boolean>();
  saveProgrammeData$ = new Subject<OutputProgrammeData>();

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  private programmeById$ = this.programmeDataService.get()
    .pipe(
      tap(programmeData => Log.info('Fetched programme data:', this, programmeData)),
      shareReplay(1)
    );

  private savedProgramme$ = this.saveProgrammeData$
    .pipe(
      mergeMap(programmeUpdate => this.programmeDataService.update(programmeUpdate)),
      tap(saved => Log.info('Updated programme:', this, saved)),
      tap(() => this.programmeSaveSuccess$.next(true)),
      tap(() => this.programmeSaveError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.programmeSaveError$.next(error.error);
        throw error;
      })
    );

  programme$ = merge(this.programmeById$, this.savedProgramme$).pipe(shareReplay(1));

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
    matDatepickerParse: ProgrammeBasicDataComponent.DATE_SHOULD_BE_VALID,
  };

  eligibleFromErrors = {
    matDatepickerParse: ProgrammeBasicDataComponent.DATE_SHOULD_BE_VALID,
    matDatepickerMax: 'programme.eligibleFrom.must.be.before.eligibleUntil',
  };

  eligibleUntilErrors = {
    matDatepickerParse: ProgrammeBasicDataComponent.DATE_SHOULD_BE_VALID,
    matDatepickerMin: 'programme.eligibleUntil.must.be.after.eligibleFrom',
  };

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService,
              public programmeEditableStateStore: ProgrammeEditableStateStore,
              private programmeDataService: ProgrammeDataService,
              private programmePageSidenavService: ProgrammePageSidenavService
  ) {
    super(changeDetectorRef, translationService);

    // todo remove after switching to app-form
    this.success$ = this.programmeSaveSuccess$.asObservable();
    this.error$ = this.programmeSaveError$.asObservable();
    combineLatest([this.changeFormState$, this.programme$])
      .pipe(untilDestroyed(this))
      .subscribe(([newState, programme]) => {
        if (newState === FormState.VIEW) {
          if (programme) {
            this.resetForm(programme);
          }
        }
        if (newState === FormState.EDIT) {
          if (programme) {
            this.resetSizedValues(programme);
          }
        }
      });
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.projectIdExample$ = combineLatest([
      this.programmeForm.controls.projectIdProgrammeAbbreviation.valueChanges.pipe(startWith('')),
      this.programmeForm.controls.projectIdUseCallId.valueChanges.pipe(startWith(true)),
      this.programme$
    ]).pipe(
      map(() => `${this.programmeForm.controls.projectIdProgrammeAbbreviation.value || ''}${this.programmeForm.controls.projectIdUseCallId.value ? '01' : ''}00001`),
    );
  }

  getForm(): FormGroup | null {
    return this.programmeForm;
  }

  resetForm(programme: OutputProgrammeData): void {
    const controls = this.programmeForm.controls;
    controls.cci.setValue(programme.cci);
    controls.title.setValue(this.getSizedValue(programme.title));
    controls.version.setValue(this.getSizedValue(programme.version));
    controls.firstYear.setValue(programme.firstYear);
    controls.lastYear.setValue(programme.lastYear);
    controls.eligibleFrom.setValue(programme.eligibleFrom);
    controls.eligibleUntil.setValue(programme.eligibleUntil);
    controls.commissionDecisionNumber.setValue(this.getSizedValue(programme.commissionDecisionNumber));
    controls.commissionDecisionDate.setValue(programme.commissionDecisionDate);
    controls.programmeAmendingDecisionNumber.setValue(this.getSizedValue(programme.programmeAmendingDecisionNumber));
    controls.programmeAmendingDecisionDate.setValue(programme.programmeAmendingDecisionDate);
    controls.projectIdProgrammeAbbreviation.setValue(programme.projectIdProgrammeAbbreviation);
    controls.projectIdUseCallId.setValue(programme.projectIdUseCallId);
  }

  protected resetSizedValues(programme: OutputProgrammeData): void {
    const controls = this.programmeForm.controls;
    controls.title.setValue(programme.title);
    controls.version.setValue(programme.version);
    controls.commissionDecisionNumber.setValue(programme.commissionDecisionNumber);
    controls.programmeAmendingDecisionNumber.setValue(programme.programmeAmendingDecisionNumber);
  }

  private submitProgrammeData(): void {
    this.submitted = true;
    const controls = this.programmeForm?.controls;

    this.saveProgrammeData$.next({
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
    } as OutputProgrammeData);
  }

  onSubmit(): void {
    Forms.confirmDialog(
      this.dialog,
      'programme.data.dialog.title',
      'programme.data.dialog.message'
    ).pipe(
      take(1),
      untilDestroyed(this),
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
