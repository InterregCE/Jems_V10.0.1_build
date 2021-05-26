import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {AbstractControl, FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {ProgrammeFundDTO} from '@cat/api';
import {FormState} from '@common/components/forms/form-state';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';
import {tap} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';

@UntilDestroy()
@Component({
  selector: 'app-programme-basic-funds',
  templateUrl: './programme-basic-funds.component.html',
  styleUrls: ['./programme-basic-funds.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeBasicFundsComponent extends ViewEditForm implements OnInit, OnChanges {

  @Input()
  programmeFunds: ProgrammeFundDTO[];

  @Output()
  saveFunds = new EventEmitter<ProgrammeFundDTO[]>();

  editableFundsForm = this.formBuilder.group({
    funds: this.formBuilder.array([]),
  });

  isProgrammeSetupLocked: boolean;

  constructor(
    private formBuilder: FormBuilder,
    protected changeDetectorRef: ChangeDetectorRef,
    protected translationService: TranslateService,
    public programmeEditableStateStore: ProgrammeEditableStateStore
  ) {
    super(changeDetectorRef, translationService);

    this.programmeEditableStateStore.isProgrammeEditableDependingOnCall$.pipe(
        tap(isProgrammeEditingLimited => this.isProgrammeSetupLocked = isProgrammeEditingLimited),
        untilDestroyed(this)
    ).subscribe();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.programmeFunds) {
      this.resetForm();
    }
  }

  get fundsForm(): FormArray {
    return this.editableFundsForm.get('funds') as FormArray;
  }

  getForm(): FormGroup | null {
    return this.editableFundsForm;
  }

  resetForm(): void {
    this.fundsForm?.clear();
    this.programmeFunds.forEach(fund => this.addControl(fund));
    this.changeFormState$.next(FormState.VIEW);
  }

  addNewFund(): void {
    this.addControl();
  }

  onSubmit(): void {
    this.saveFunds.emit(this.editableFundsForm.controls.funds.value.map((fund: any) => ({
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
