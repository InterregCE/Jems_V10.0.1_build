import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ViewEditFormComponent} from '@common/components/forms/view-edit-form.component';
import {AbstractControl, FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {ProgrammeFundDTO, ProgrammeFundService} from '@cat/api';
import {FormState} from '@common/components/forms/form-state';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {catchError, map, mergeMap, share, shareReplay, tap} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {combineLatest, merge, Subject} from 'rxjs';
import {MatSort} from '@angular/material/sort';
import {Log} from '@common/utils/log';
import {HttpErrorResponse} from '@angular/common/http';
import {APIError} from '@common/models/APIError';
import {ProgrammeEditableStateStore} from '../programme-page/services/programme-editable-state-store.service';
import {ProgrammePageSidenavService} from '../programme-page/services/programme-page-sidenav.service';

@UntilDestroy()
@Component({
  selector: 'jems-programme-funds',
  templateUrl: './programme-funds.component.html',
  styleUrls: ['./programme-funds.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeFundsComponent extends ViewEditFormComponent implements OnInit {

  fundsSaveError$ = new Subject<APIError | null>();
  fundsSaveSuccess$ = new Subject<boolean>();
  saveFunds$ = new Subject<ProgrammeFundDTO[]>();

  newPageSize$ = new Subject<number>();
  newPageIndex$ = new Subject<number>();
  newSort$ = new Subject<Partial<MatSort>>();

  private initialFunds$ = this.programmeFundService.getProgrammeFundList()
    .pipe(
      tap(funds => Log.info('Fetched programme funds:', this, funds)),
      shareReplay(1)
    );

  private savedFunds$ = this.saveFunds$
    .pipe(
      mergeMap(funds => this.programmeFundService.updateProgrammeFundList(funds)),
      tap(saved => Log.info('Updated programme funds:', this, saved)),
      tap(() => this.fundsSaveSuccess$.next(true)),
      tap(() => this.fundsSaveError$.next(null)),
      catchError((error: HttpErrorResponse) => {
        this.fundsSaveError$.next(error.error);
        throw error;
      })
    );

  funds$ = merge(this.initialFunds$, this.savedFunds$)
    .pipe(
      map(funds => funds.map(fund => ({
        id: fund.id,
        selected: fund.selected,
        type: fund.type,
        abbreviation: fund.abbreviation,
        description: fund.description
      }))),
      share()
    );

  editableFundsForm = this.formBuilder.group({
    funds: this.formBuilder.array([]),
  });

  isProgrammeSetupLocked: boolean;

  constructor(
    private formBuilder: FormBuilder,
    protected changeDetectorRef: ChangeDetectorRef,
    protected translationService: TranslateService,
    public programmeEditableStateStore: ProgrammeEditableStateStore,
    private programmeFundService: ProgrammeFundService,
    private programmePageSidenavService: ProgrammePageSidenavService
  ) {
    super(changeDetectorRef, translationService);

    this.programmeEditableStateStore.isProgrammeEditableDependingOnCall$.pipe(
      tap(isProgrammeEditingLimited => this.isProgrammeSetupLocked = isProgrammeEditingLimited),
      untilDestroyed(this)
    ).subscribe();

    // todo remove after switching to jems-form
    this.success$ = this.fundsSaveSuccess$.asObservable();
    this.error$ = this.fundsSaveError$.asObservable();
    combineLatest([this.changeFormState$, this.funds$])
      .pipe(untilDestroyed(this))
      .subscribe(([newState, funds]) => {
        if (newState === FormState.VIEW) {
          if (funds) {
            this.resetForm(funds);
            this.editableFundsForm.disable();
          }
        }
      });
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  get fundsForm(): FormArray {
    return this.editableFundsForm.get('funds') as FormArray;
  }

  getForm(): FormGroup | null {
    return this.editableFundsForm;
  }

  resetForm(funds: ProgrammeFundDTO[]): void {
    this.fundsForm?.clear();
    funds.forEach(fund => this.addControl(fund));
  }

  addNewFund(): void {
    this.addControl();
  }

  onSubmit(): void {
    this.saveFunds$.next(this.editableFundsForm.controls.funds.value.map((fund: any) => ({
      id: fund.id,
      selected: fund.selected === undefined ? true : fund.selected,
      type: fund.type,
      abbreviation: fund.abbreviation,
      description: fund.description,
    })));
  }

  isPredefinedFund(formGroup: AbstractControl): boolean {
    return formGroup.get('type')?.value !== ProgrammeFundDTO.TypeEnum.OTHER;
  }

  private addControl(fund?: ProgrammeFundDTO): void {
    this.fundsForm.push(this.formBuilder.group({
      id: this.formBuilder.control(fund?.id || null),
      selected: this.formBuilder.control(fund?.selected || false),
      type: this.formBuilder.control(fund?.type || ProgrammeFundDTO.TypeEnum.OTHER),
      abbreviation: this.formBuilder.control(fund?.abbreviation || []),
      description: this.formBuilder.control(fund?.description || []),
    }));
  }

  protected enterEditMode(): void {
    this.fundsForm.controls.forEach(control => {
      if (this.isProgrammeSetupLocked && control.get('selected')?.value) {
        control.get('selected')?.disable();
      }
    });
    this.changeDetectorRef.markForCheck();
  }
}
