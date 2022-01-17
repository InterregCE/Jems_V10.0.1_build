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
import {ViewEditFormComponent} from '@common/components/forms/view-edit-form.component';
import {ProgrammeStateAidDTO, ProgrammeStateAidUpdateDTO} from '@cat/api';
import {FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {TranslateService} from '@ngx-translate/core';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';
import {tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {FormState} from '@common/components/forms/form-state';
import {ProgrammeStateAidConstants, ProgrammeStateAidMeasureRelation} from './constants/programme-state-aid-constants';
import {TableConfig} from '@common/directives/table-config/TableConfig';

@UntilDestroy()
@Component({
  selector: 'app-programme-state-aid-list',
  templateUrl: './programme-state-aid-list.component.html',
  styleUrls: ['./programme-state-aid-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeStateAidListComponent extends ViewEditFormComponent implements OnInit, OnChanges {
  programmeStateAidConstants = ProgrammeStateAidConstants;

  @Input()
  stateAids: ProgrammeStateAidDTO[];

  @Output()
  saveStateAids = new EventEmitter<ProgrammeStateAidUpdateDTO>();

  isProgrammeSetupRestricted: boolean;
  toDeleteIds: number[] = [];

  stateAidForm: FormGroup;
  measureRelations = this.programmeStateAidConstants.stateAidMeasures;
  initialSelectedMeasures: ProgrammeStateAidDTO.MeasureEnum[];
  filteredMeasureRelations: ProgrammeStateAidMeasureRelation[];
  tableConfig: TableConfig[];

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
    this.stateAidForm = this.formBuilder.group({
      stateAids: this.formBuilder.array([]),
    });
    this.resetForm();
    this.initialSelectedMeasures = this.stateAids.map(stateAid => stateAid.measure);
    this.filteredMeasureRelations = this.measureRelations.filter(value => this.initialSelectedMeasures.indexOf(value.measure) < 0);

    this.changeFormState$
      .pipe(
        untilDestroyed(this),
      ).subscribe(state => this.tableConfig = [{minInRem: 19},{minInRem: 21},{minInRem: 21},{minInRem: 7},{minInRem: 6, maxInRem: 6},{minInRem: 8, maxInRem: 8},{minInRem: 40, maxInRem: 40}, ...state === FormState.EDIT ? [{maxInRem: 3, minInRem: 3}] : []]);
  }

  get stateAidsForm(): FormArray {
    return this.stateAidForm.get('stateAids') as FormArray;
  }

  getForm(): FormGroup | null {
    return this.stateAidForm;
  }

  resetForm(): void {
    this.stateAidsForm.clear();
    this.stateAids.forEach(stateAid => this.addControl(stateAid));
    this.changeFormState$.next(FormState.VIEW);
  }

  addNewStateAid(): void {
    this.addControl();
  }

  addNewStateAidAndChangeFormState(): void {
    this.changeFormState$.next(FormState.EDIT);
    this.addControl();
  }

  addControl(stateAid?: ProgrammeStateAidDTO): void {
    this.stateAidsForm.push(this.formBuilder.group({
      id: [stateAid?.id],
      measure: [stateAid?.measure ? this.measureRelations.filter(relation => relation.measure === stateAid.measure)[0] : ''],
      name: [stateAid?.name || []],
      abbreviatedName: [stateAid?.abbreviatedName || []],
      schemeNumber: [stateAid?.schemeNumber || '', Validators.maxLength(25)],
      maxIntensity: [stateAid?.maxIntensity || null],
      threshold: [stateAid?.threshold || null],
      comments: [stateAid?.comments || []],
    }));
  }

  deleteStateAid(elementIndex: number, elementId?: number): void {
    this.stateAidsForm.removeAt(elementIndex);
    if (elementId) {
      this.toDeleteIds.push(elementId);
    }
    this.rebuildFilteredList();
  }

  onSubmit(): void {
    this.saveStateAids.emit({
      toPersist: this.stateAidForm.getRawValue().stateAids
        .map((sa: any) => ({
          id: sa.id,
          measure: sa.measure.measure,
          name: sa.name,
          abbreviatedName: sa.abbreviatedName,
          schemeNumber: sa.schemeNumber,
          maxIntensity: sa.maxIntensity,
          threshold: sa.threshold,
          comments: sa.comments,
          })
        ),
      toDeleteIds: this.toDeleteIds
    });
  }

  protected enterEditMode(): void {
    this.toDeleteIds = [];
    if (this.isProgrammeSetupRestricted) {
      this.stateAidsForm.controls.forEach(control => control.get('measure')?.disable());
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.stateAids && this.stateAidsForm) {
      this.resetForm();
    }
  }

  displayFn(relation: ProgrammeStateAidMeasureRelation): string {
    return relation && relation.measure ? relation.measureDisplayValue : '';
  }

  relationChanged(relation: ProgrammeStateAidMeasureRelation, form: FormGroup): void {
    this.rebuildFilteredList();
    form.controls.name.patchValue(relation.name ? relation.name : []);
    form.controls.abbreviatedName.patchValue(relation.abbreviatedName ? relation.abbreviatedName : []);
    form.controls.threshold.patchValue(relation.threshold || null);
    form.controls.maxIntensity.patchValue(relation.maxIntensity || null);
    form.controls.comments.patchValue(relation.comments ? relation.comments : []);
    form.controls.schemeNumber.patchValue(null);
  }

  selectionUnfocused(event: FocusEvent, form: FormGroup): void {
    if (this.selectOptionClicked(event)) {
      return;
    }
    const selected = this.findByMeasure(form.controls.measure.value, this.measureRelations);
    if (!selected) {
      form.controls.measure.patchValue('');
      this.rebuildFilteredList();
    }
  }

  filteredStateAidMeasureValues(index: number): ProgrammeStateAidMeasureRelation[] {
    return this.filter(this.stateAidsForm.at(index).value.measure, this.filteredMeasureRelations);
  }

  addStateAidVisible(): boolean {
    return this.stateAidsForm.length < 20;
  }

  private selectOptionClicked(event: FocusEvent): boolean {
    return !!event.relatedTarget && (event.relatedTarget as any).tagName === 'MAT-OPTION';
  }

  private findByMeasure(value: ProgrammeStateAidMeasureRelation, measures: ProgrammeStateAidMeasureRelation[]): ProgrammeStateAidMeasureRelation | undefined {
    return measures.find(measure => value === measure);
  }

  private filter(value: string | ProgrammeStateAidMeasureRelation, measures: ProgrammeStateAidMeasureRelation[]): ProgrammeStateAidMeasureRelation[] {
    let filterValue = '';
    if (typeof value === 'string') {
      filterValue = (value || '').toLowerCase();
    }
    else {
      filterValue = value?.measureDisplayValue || '';
    }
    if (filterValue === '') {
      return measures;
    }
    return measures.filter(measure => measure.measureDisplayValue.toLowerCase().includes(filterValue));
  }

  private rebuildFilteredList(): void {
    const existingSelections = this.stateAidForm.controls.stateAids.value.map((sa: any) => sa.measure);
    this.filteredMeasureRelations = this.measureRelations.filter(sa => existingSelections.indexOf(sa) < 0);
  }
}
