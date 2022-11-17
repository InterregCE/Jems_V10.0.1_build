import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output, SimpleChanges
} from '@angular/core';
import {ViewEditFormComponent} from '@common/components/forms/view-edit-form.component';
import {
  TypologyErrorsDTO,
  TypologyErrorsUpdateDTO
} from '@cat/api';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';
import {tap} from 'rxjs/operators';
import {FormState} from '@common/components/forms/form-state';

@UntilDestroy()
@Component({
  selector: 'jems-programme-typology-errors-list',
  templateUrl: './programme-typology-errors-list-component.html',
  styleUrls: ['./programme-typology-errors-list-component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeTypologyErrorsListComponent extends ViewEditFormComponent implements OnInit, OnChanges {

  @Input()
  typologyErrors: TypologyErrorsDTO[];

  @Output()
  saveTypologyErrors = new EventEmitter<TypologyErrorsUpdateDTO>();

  isProgrammeSetupRestricted: boolean;
  toDeleteIds: number[] = [];

  formGroup = new FormGroup({});

  constructor(protected changeDetectorRef: ChangeDetectorRef,
              private formBuilder: FormBuilder,
              protected translationService: TranslateService,
              public programmeEditableStateStore: ProgrammeEditableStateStore) {
    super(changeDetectorRef, translationService);
    this.programmeEditableStateStore.isProgrammeEditableDependingOnCall$.pipe(
      tap(isProgrammeEditingLimited => this.isProgrammeSetupRestricted = isProgrammeEditingLimited),
      untilDestroyed(this)
    ).subscribe();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.formGroup = this.formBuilder.group({
      typologyErrors: this.formBuilder.array([]),
    });
    this.resetTypologyErrorsForm();
  }

  get typologyErrorsForm(): FormArray {
    return this.formGroup.get('typologyErrors') as FormArray;
  }

  getForm(): FormGroup | null {
    return this.formGroup;
  }

  resetTypologyErrorsForm(): void {
    this.typologyErrorsForm.clear();
    this.typologyErrors.forEach(typologyError => this.addControl(typologyError));
    this.changeFormState$.next(FormState.VIEW);
  }

  addNewTypologyError(): void {
    this.addControl();
  }

  addControl(typologyErrors?: TypologyErrorsDTO): void {
    this.typologyErrorsForm.push(this.formBuilder.group({
      id: [typologyErrors?.id],
      description: [typologyErrors?.description || [], Validators.maxLength(500)],
    }));
  }

  deleteTypologyError(elementIndex: number, elementId?: number): void {
    this.typologyErrorsForm.removeAt(elementIndex);
    if (elementId) {
      this.toDeleteIds.push(elementId);
    }
  }

  onTypologyErrorsSubmit(): void {
    this.saveTypologyErrors.emit({
      toPersist: this.formGroup.controls.typologyErrors.value
        .map((ls: any) => ({
            id: ls.id,
            description: ls.description,
          })
        ),
      toDeleteIds: this.toDeleteIds
    });
  }

  protected enterEditMode(): void {
    this.toDeleteIds = [];
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.typologyErrors && this.typologyErrorsForm) {
      this.resetTypologyErrorsForm();
    }
  }

}
