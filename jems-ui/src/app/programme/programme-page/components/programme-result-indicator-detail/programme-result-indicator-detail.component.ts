import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ViewEditFormComponent} from '@common/components/forms/view-edit-form.component';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {filter, take, takeUntil, tap} from 'rxjs/operators';
import {FormState} from '@common/components/forms/form-state';
import {Forms} from '@common/utils/forms';
import {
  InputTranslation,
  ProgrammePriorityDTO,
  ResultIndicatorCreateRequestDTO,
  ResultIndicatorDetailDTO,
  ResultIndicatorUpdateRequestDTO
} from '@cat/api';
import {
  ProgrammeResultIndicatorConstants,
  ResultIndicatorCodeRelation
} from './constants/programme-result-indicator-constants';
import {TranslateService} from '@ngx-translate/core';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';
import {LanguageStore} from '@common/services/language-store.service';
@UntilDestroy()
@Component({
  selector: 'app-programme-result-indicator-detail',
  templateUrl: './programme-result-indicator-detail.component.html',
  styleUrls: ['./programme-result-indicator-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeResultIndicatorDetailComponent extends ViewEditFormComponent implements OnInit {

  programmeResultIndicatorConstants = ProgrammeResultIndicatorConstants;
  isProgrammeSetupLocked: boolean;
  baselineOption = {min: 0};

  @Input()
  resultIndicator: ResultIndicatorDetailDTO;
  @Input()
  priorities: ProgrammePriorityDTO[];
  @Input()
  isCreate: boolean;
  @Output()
  createResultIndicator: EventEmitter<ResultIndicatorCreateRequestDTO> = new EventEmitter<ResultIndicatorCreateRequestDTO>();
  @Output()
  updateResultIndicator: EventEmitter<ResultIndicatorUpdateRequestDTO> = new EventEmitter<ResultIndicatorUpdateRequestDTO>();
  @Output()
  cancelCreate: EventEmitter<void> = new EventEmitter<void>();

  indicatorCodes = this.programmeResultIndicatorConstants.indicatorCodes;

  resultIndicatorForm = this.formBuilder.group({
    identifier: ['', [Validators.required, Validators.maxLength(10)]],
    indicatorCode: ['', Validators.maxLength(6)],
    indicatorName: [[]],
    specificObjective: ['', Validators.required],
    measurementUnit: [[]],
    baseline: [0],
    referenceYear: ['', Validators.maxLength(10)],
    finalTarget: [0],
    sourceOfData: [[]],
    comments: ['', Validators.maxLength(1000)]
  });

  inputErrorMessages = {
    required: 'common.error.field.blank',
    maxlength: 'common.error.field.max.length',
  };

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              public programmeEditableStateStore: ProgrammeEditableStateStore,
              private languageStore: LanguageStore,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService) {
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

  resetForm(): void {
    if (!this.isCreate) {
      this.resultIndicatorForm.controls.identifier.setValue(this.resultIndicator.identifier);
      this.resultIndicatorForm.controls.indicatorCode.setValue(
        this.indicatorCodes.find(relation => relation.code === this.resultIndicator.code)
      );
      this.resultIndicatorForm.controls.indicatorName.setValue(this.resultIndicator.name);
      this.resultIndicatorForm.controls.specificObjective.setValue(this.resultIndicator.programmePriorityPolicySpecificObjective);
      this.resultIndicatorForm.controls.measurementUnit.setValue(this.resultIndicator.measurementUnit);
      this.resultIndicatorForm.controls.baseline.setValue(this.resultIndicator.baseline || 0);
      this.resultIndicatorForm.controls.referenceYear.setValue(this.resultIndicator.referenceYear);
      this.resultIndicatorForm.controls.finalTarget.setValue(this.resultIndicator.finalTarget || 0);
      this.resultIndicatorForm.controls.sourceOfData.setValue(this.resultIndicator.sourceOfData);
      this.resultIndicatorForm.controls.comments.setValue(this.resultIndicator.comment);
      this.changeFormState$.next(FormState.VIEW);
    } else {
      this.changeFormState$.next(FormState.EDIT);
    }
  }

  getForm(): FormGroup | null {
    return this.resultIndicatorForm;
  }

  onSubmit(): void {
    Forms.confirmDialog(
      this.dialog,
      this.isCreate ? 'result.indicator.final.dialog.title.save' : 'result.indicator.final.dialog.title.update',
      this.isCreate ? 'result.indicator.final.dialog.message.save' : 'result.indicator.final.dialog.message.update'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() => {
      if (this.isCreate) {
        this.createResultIndicator.emit({
          identifier: this.resultIndicatorForm?.controls?.identifier?.value,
          code: this.resultIndicatorForm?.controls?.indicatorCode?.value?.code,
          name: this.resultIndicatorForm?.controls?.indicatorName?.value,
          programmeObjectivePolicy: this.resultIndicatorForm?.controls?.specificObjective?.value,
          measurementUnit: this.resultIndicatorForm?.controls?.measurementUnit?.value,
          baseline: this.resultIndicatorForm?.controls?.baseline?.value,
          referenceYear: this.resultIndicatorForm?.controls?.referenceYear?.value,
          finalTarget: this.resultIndicatorForm?.controls?.finalTarget?.value,
          sourceOfData: this.resultIndicatorForm?.controls?.sourceOfData?.value,
          comment: this.resultIndicatorForm?.controls?.comments?.value,
        });
      } else {
        this.updateResultIndicator.emit({
          id: this.resultIndicator?.id,
          identifier: this.resultIndicatorForm?.controls?.identifier?.value,
          code: this.resultIndicatorForm?.controls?.indicatorCode?.value?.code,
          name: this.resultIndicatorForm?.controls?.indicatorName?.value,
          programmeObjectivePolicy: this.resultIndicatorForm?.controls?.specificObjective?.value,
          measurementUnit: this.resultIndicatorForm?.controls?.measurementUnit?.value,
          baseline: this.resultIndicatorForm?.controls?.baseline?.value,
          referenceYear: this.resultIndicatorForm?.controls?.referenceYear?.value,
          finalTarget: this.resultIndicatorForm?.controls?.finalTarget?.value,
          sourceOfData: this.resultIndicatorForm?.controls?.sourceOfData?.value,
          comment: this.resultIndicatorForm?.controls?.comments?.value,
        });
      }
    });
  }

  onCancel(): void {
    if (this.isCreate) {
      this.cancelCreate.emit();
    } else {
      this.resetForm();
    }
  }

  updateIndicatorCode(indicatorCode: ResultIndicatorCodeRelation): void {
    this.resultIndicatorForm.get('indicatorName')?.setValue(this.extractFromCodeRelation(indicatorCode, code => code.name));
    this.resultIndicatorForm.get('measurementUnit')?.setValue(this.extractFromCodeRelation(indicatorCode, code => code.measurementUnit));
  }

  protected enterEditMode(): void {
    if (this.isProgrammeSetupLocked && !this.isCreate) {
      this.resultIndicatorForm.controls.indicatorCode.disable();
      this.resultIndicatorForm.controls.specificObjective.disable();
      this.baselineOption = {min: this.resultIndicator.baseline};
    }
  }

  private _filter(value: string, source: ResultIndicatorCodeRelation[]): ResultIndicatorCodeRelation[] {
    const filterValue = value.toLowerCase();
    return source.filter(option => option.code.toLowerCase().includes(filterValue));
  }

  extractFromCodeRelation(codeRelation: ResultIndicatorCodeRelation, extract: (f: ResultIndicatorCodeRelation) => string): InputTranslation[] {
    if (!codeRelation) {
      return [];
    }

    return this.languageStore.getSystemLanguagesValue().map(language => ({
      language,
      translation: extract(codeRelation),
    } as InputTranslation));
  }

}
