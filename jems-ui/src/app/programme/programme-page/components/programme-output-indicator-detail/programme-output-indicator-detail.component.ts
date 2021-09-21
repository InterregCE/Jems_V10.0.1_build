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
import {
  InputTranslation,
  OutputIndicatorCreateRequestDTO,
  OutputIndicatorDetailDTO,
  OutputIndicatorUpdateRequestDTO,
  ProgrammeIndicatorService,
  ProgrammePriorityDTO,
  ResultIndicatorDetailDTO,
} from '@cat/api';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {combineLatest, Observable} from 'rxjs';
import {filter, map, startWith, take, takeUntil, tap, withLatestFrom} from 'rxjs/operators';
import {FormState} from '@common/components/forms/form-state';
import {
  OutputIndicatorCodeRelation,
  ProgrammeOutputIndicatorConstants
} from './constants/programme-output-indicator-constants';
import {Forms} from '../../../../common/utils/forms';
import {Log} from '../../../../common/utils/log';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';
import {TranslateService} from '@ngx-translate/core';
import {LanguageStore} from '../../../../common/services/language-store.service';

@UntilDestroy()
@Component({
  selector: 'app-programme-output-indicator-detail',
  templateUrl: './programme-output-indicator-detail.component.html',
  styleUrls: ['./programme-output-indicator-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeOutputIndicatorDetailComponent extends ViewEditForm implements OnInit {

  programmeOutputIndicatorConstants = ProgrammeOutputIndicatorConstants;
  isProgrammeSetupLocked: boolean;

  @Input()
  outputIndicator: OutputIndicatorDetailDTO;
  @Input()
  priorities: Array<ProgrammePriorityDTO>;
  @Input()
  isCreate: boolean;
  @Output()
  createOutputIndicator: EventEmitter<OutputIndicatorCreateRequestDTO> = new EventEmitter<OutputIndicatorCreateRequestDTO>();
  @Output()
  updateOutputIndicator: EventEmitter<OutputIndicatorUpdateRequestDTO> = new EventEmitter<OutputIndicatorUpdateRequestDTO>();
  @Output()
  cancelCreate: EventEmitter<void> = new EventEmitter<void>();

  indicatorCodes = this.programmeOutputIndicatorConstants.indicatorCodes;

  resultIndicators$: Observable<ResultIndicatorDetailDTO[]> = this.programmeIndicatorService.getResultIndicatorDetails(0, 50, 'DESC').pipe(
    tap(resultIndicator => Log.info('Fetched result Indicator data:', this, resultIndicator)),
    map(page => page.content));

  filteredResultIndicators$: Observable<ResultIndicatorDetailDTO[]>;

  outputIndicatorForm = this.formBuilder.group({
    identifier: ['', [Validators.required, Validators.maxLength(5)]],
    indicatorCode: ['', Validators.maxLength(6)],
    indicatorName: [[]],
    specificObjective: ['', Validators.required],
    measurementUnit: [[]],
    milestone: [0],
    finalTarget: [0],
    resultIndicatorId: [null]
  });

  inputErrorMessages = {
    required: 'common.error.field.blank',
    maxlength: 'common.error.field.max.length',
  };

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService,
              private languageStore: LanguageStore,
              private programmeIndicatorService: ProgrammeIndicatorService,
              public programmeEditableStateStore: ProgrammeEditableStateStore,
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

  resetForm(): void {
    this.filteredResultIndicators$ = combineLatest([this.resultIndicators$, this.outputIndicatorForm.controls.specificObjective.valueChanges.pipe(startWith(this.outputIndicator.programmePriorityPolicySpecificObjective))])
      .pipe(
        map(([resultIndicators, specificObjective]) => resultIndicators.filter((it: ResultIndicatorDetailDTO) => it.programmePriorityPolicySpecificObjective === specificObjective) || []),
      );

    this.outputIndicatorForm.controls.specificObjective.valueChanges.pipe(
      takeUntil(this.destroyed$),
      withLatestFrom(this.filteredResultIndicators$),
      tap(([, filteredResultIndicators]) => {
        if (filteredResultIndicators.every(it => it.id !== this.outputIndicatorForm.controls.resultIndicatorId.value)) {
          this.outputIndicatorForm.controls.resultIndicatorId.setValue(null);
        }
      })
    ).subscribe();

    if (this.isCreate) {
      this.changeFormState$.next(FormState.EDIT);
    } else {
      this.outputIndicatorForm.controls.identifier.setValue(this.outputIndicator.identifier);
      this.outputIndicatorForm.controls.indicatorCode.setValue(
        this.indicatorCodes.find(relation => relation.code === this.outputIndicator.code)
      );
      this.outputIndicatorForm.controls.indicatorName.setValue(this.outputIndicator.name);
      this.outputIndicatorForm.controls.specificObjective.setValue(this.outputIndicator.programmePriorityPolicySpecificObjective);
      this.outputIndicatorForm.controls.measurementUnit.setValue(this.outputIndicator.measurementUnit);
      this.outputIndicatorForm.controls.milestone.setValue(this.outputIndicator.milestone || 0);
      this.outputIndicatorForm.controls.finalTarget.setValue(this.outputIndicator.finalTarget || 0);
      this.outputIndicatorForm.controls.resultIndicatorId.setValue(this.outputIndicator.resultIndicatorId);
      this.changeFormState$.next(FormState.VIEW);
    }
  }

  getForm(): FormGroup | null {
    return this.outputIndicatorForm;
  }

  onSubmit(): void {
    Forms.confirmDialog(
      this.dialog,
      this.isCreate ? 'output.indicator.final.dialog.title.save' : 'output.indicator.final.dialog.title.update',
      this.isCreate ? 'output.indicator.final.dialog.message.save' : 'output.indicator.final.dialog.message.update'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() => {
      if (this.isCreate) {
        this.createOutputIndicator.emit({
          identifier: this.outputIndicatorForm?.controls?.identifier?.value,
          code: this.outputIndicatorForm?.controls?.indicatorCode?.value?.code,
          name: this.outputIndicatorForm?.controls?.indicatorName?.value,
          programmeObjectivePolicy: this.outputIndicatorForm?.controls?.specificObjective?.value,
          measurementUnit: this.outputIndicatorForm?.controls?.measurementUnit?.value,
          milestone: this.outputIndicatorForm?.controls?.milestone?.value,
          finalTarget: this.outputIndicatorForm?.controls?.finalTarget?.value,
          resultIndicatorId: this.outputIndicatorForm?.controls?.resultIndicatorId.value
        });
      } else {
        this.updateOutputIndicator.emit({
          id: this.outputIndicator?.id,
          identifier: this.outputIndicatorForm?.controls?.identifier?.value,
          code: this.outputIndicatorForm?.controls?.indicatorCode?.value?.code,
          name: this.outputIndicatorForm?.controls?.indicatorName?.value,
          programmeObjectivePolicy: this.outputIndicatorForm?.controls?.specificObjective?.value,
          measurementUnit: this.outputIndicatorForm?.controls?.measurementUnit?.value,
          milestone: this.outputIndicatorForm?.controls?.milestone?.value,
          finalTarget: this.outputIndicatorForm?.controls?.finalTarget?.value,
          resultIndicatorId: this.outputIndicatorForm?.controls?.resultIndicatorId.value
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

  updateIndicatorCode(indicatorCode: OutputIndicatorCodeRelation): void {
    this.outputIndicatorForm.get('indicatorName')?.setValue(this.extractFromCodeRelation(indicatorCode, code => code.name));
    this.outputIndicatorForm.get('measurementUnit')?.setValue(this.extractFromCodeRelation(indicatorCode, code => code.measurementUnit));
  }

  protected enterEditMode(): void {
    if (this.isProgrammeSetupLocked && !this.isCreate) {
      this.outputIndicatorForm.controls.indicatorCode.disable();
      this.outputIndicatorForm.controls.specificObjective.disable();
    }
  }

  private _filter(value: string, source: OutputIndicatorCodeRelation[]): OutputIndicatorCodeRelation[] {
    const filterValue = value.toLowerCase();
    return source.filter(option => option.code.toLowerCase().includes(filterValue));
  }

  extractFromCodeRelation(codeRelation: OutputIndicatorCodeRelation, extract: (f: OutputIndicatorCodeRelation) => string): InputTranslation[] {
    if (!codeRelation) {
      return [];
    }

    return this.languageStore.getSystemLanguagesValue().map(language => ({
      language,
      translation: extract(codeRelation),
    } as InputTranslation));
  }

}
