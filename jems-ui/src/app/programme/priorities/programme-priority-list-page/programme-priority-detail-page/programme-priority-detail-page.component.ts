import {ChangeDetectorRef, Component} from '@angular/core';
import {ProgrammePriorityDetailPageStore} from './programme-priority-detail-page-store.service';
import {ProgrammePageSidenavService} from '../../../programme-page/services/programme-page-sidenav.service';
import {ActivatedRoute} from '@angular/router';
import {combineLatest, Observable, of} from 'rxjs';
import {ProgrammePriorityDTO, ProgrammeSpecificObjectiveDTO} from '@cat/api';
import {catchError, filter, map, take, tap} from 'rxjs/operators';
import {AbstractControl, FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {ProgrammePriorityDetailPageConstants} from './programme-priority-detail-page.constants';
import {Alert} from '@common/components/forms/alert';
import {Forms} from '@common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {HttpErrorResponse} from '@angular/common/http';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProgrammeEditableStateStore} from '../../../programme-page/services/programme-editable-state-store.service';
import {APIError} from '@common/models/APIError';
import {PartiallyLockableOption} from '@common/components/filter/filter-autocomplete-input/partially-lockable-option';
import {animate, state, style, transition, trigger} from '@angular/animations';

@UntilDestroy()
@Component({
  selector: 'jems-programme-priority-detail-page',
  templateUrl: './programme-priority-detail-page.component.html',
  styleUrls: ['./programme-priority-detail-page.component.scss'],
  providers: [ProgrammePriorityDetailPageStore],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class ProgrammePriorityDetailPageComponent {
  Alert = Alert;
  constants = ProgrammePriorityDetailPageConstants;
  priorityId = this.activatedRoute.snapshot.params.priorityId;
  objectivePoliciesAlreadyInUse: string[] = [];
  isProgrammeSetupLocked: boolean;
  tableData: AbstractControl[] = [];
  columnsToDisplay = ['policySelected', 'programmeCode', 'priorityObjectiveTitle', 'priorityOfficialCode', 'expandPriority'];
  expandedElement: ProgrammePriorityDTO | null;

  data$: Observable<{
    priority: ProgrammePriorityDTO | {};
    objectives: string[];
    freePrioritiesWithPolicies: { [key: string]: ProgrammeSpecificObjectiveDTO[] };
  }>;

  form = this.formBuilder.group({
    code: this.formBuilder.control('', this.constants.CODE.validators),
    officialCode: this.formBuilder.control(''),
    title: this.formBuilder.control([], this.constants.TITLE.validators),
    objective: this.formBuilder.control('', this.constants.OBJECTIVE.validators),
    specificObjectives: this.formBuilder.array([], {validators: this.constants.mustHaveSpecificObjectiveSelected})
  });

  dimensions = [
    'TypesOfIntervention',
    'FormOfSupport',
    'TerritorialDeliveryMechanism',
    'EconomicActivity',
    'GenderEquality',
    'RegionalAndSeaBasinStrategy'
  ];
  dimensionCodes = new Map(
    Array.from({length: 182}, (x, i) => i + 1)
      .map(i => [String(i).padStart(3, '0'), String(i).padStart(3, '0')])
  );

  // TODO: remove when new edit mode is introduced
  saveSuccess: string;
  saveError: APIError;

  constructor(private programmePageSidenavService: ProgrammePageSidenavService,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder,
              private changeDetectorRef: ChangeDetectorRef, // TODO: remove when new edit mode is introduced
              private dialog: MatDialog,
              public programmeEditableStateStore: ProgrammeEditableStateStore,
              public pageStore: ProgrammePriorityDetailPageStore) {

    this.data$ = combineLatest([
      this.pageStore.priority$,
      this.pageStore.policies$,
    ]).pipe(
      tap(([priority, setup]) =>
        this.objectivePoliciesAlreadyInUse = setup.objectivePoliciesAlreadyInUse as string[]
      ),
      map(([priority, setup]) => ({
          priority,
          objectives: this.getAvailableObjectives((priority as ProgrammePriorityDTO).objective, setup.freePrioritiesWithPolicies),
          freePrioritiesWithPolicies: setup.freePrioritiesWithPolicies,
          objectivePoliciesAlreadyInUse: setup.objectivePoliciesAlreadyInUse as string[]
        })
      ),
      tap(data => this.resetForm(data.priority as ProgrammePriorityDTO, data.freePrioritiesWithPolicies)),
      tap(data => (data.priority as ProgrammePriorityDTO)?.id ? this.form.disable() : this.form.enable())
    );

    this.programmeEditableStateStore.isProgrammeEditableDependingOnCall$.pipe(
      tap(isProgrammeEditingLimited => this.isProgrammeSetupLocked = isProgrammeEditingLimited),
      untilDestroyed(this)
    ).subscribe();
  }

  get specificObjectives(): FormArray {
    return this.form.get(this.constants.SPECIFIC_OBJECTIVES.name) as FormArray;
  }

  save(): void {
    this.form.enable();
    this.specificObjectives.controls.forEach(control => control.get('selected')?.disable());

    const priority: ProgrammePriorityDTO = this.form.value;
    priority.objective = this.form.get(this.constants.OBJECTIVE.name)?.value;
    priority.specificObjectives = this.specificObjectives.controls
      .filter(control => !!control.get(this.constants.POLICY_SELECTED.name)?.value)
      .map(control => this.transformSpecificObjectiveValues(control));

    if (!this.priorityId) {
      this.pageStore.createPriority(priority)
        .pipe(
          take(1),
          tap(() => this.programmePageSidenavService.goToPriorities()),
          catchError(err => this.handleError(err, priority))
        ).subscribe();
    } else {
      this.saveSuccess = '';
      this.pageStore.updatePriority(this.priorityId, priority)
        .pipe(
          take(1),
          tap(() => this.handleSuccess()),
          catchError(err => this.handleError(err, priority))
        ).subscribe();
    }
  }

  resetForm(priority: ProgrammePriorityDTO,
            freePrioritiesWithPolicies: { [key: string]: ProgrammeSpecificObjectiveDTO[] }): void {
    this.form.patchValue(priority);
    this.changeCurrentObjective(priority?.objective, freePrioritiesWithPolicies, priority.specificObjectives);
    this.tableData = [...this.specificObjectives.controls];
  }

  cancel(priority: ProgrammePriorityDTO,
         freePrioritiesWithPolicies: { [key: string]: ProgrammeSpecificObjectiveDTO[] }): void {
    if (!this.priorityId) {
      this.programmePageSidenavService.goToPriorities();
      return;
    }
    this.resetForm(priority, freePrioritiesWithPolicies);
    this.form.disable(); // TODO: remove when new edit mode is introduced
  }

  submit(): void {
    if (this.priorityId) {
      this.save();
      return;
    }

    // TODO: probably remove when new edit mode is introduced?
    Forms.confirmDialog(
      this.dialog,
      'programme.priority.dialog.title',
      'programme.priority.dialog.message'
    ).pipe(
      take(1),
      filter(yes => !!yes)
    ).subscribe(() => this.save());
  }

  changeCurrentObjective(objective: ProgrammePriorityDTO.ObjectiveEnum,
                         freePrioritiesWithPolicies: { [p: string]: ProgrammeSpecificObjectiveDTO[] },
                         selectedSpecificObjectives?: ProgrammeSpecificObjectiveDTO[]): void {
    this.specificObjectives.clear();
    selectedSpecificObjectives?.forEach(
      selected => this.addSpecificObjective(selected, true)
    );
    const freePolicies = freePrioritiesWithPolicies[objective];
    freePolicies?.forEach(policy => this.addSpecificObjective(policy, false));
    this.tableData = [...this.specificObjectives.controls];
  }

  setCheckedStatus(specificObjectiveIndex: number, checked: boolean): void {
    this.specificObjectives.at(specificObjectiveIndex).get(this.constants.POLICY_SELECTED.name)?.patchValue(checked);
    this.form.updateValueAndValidity();
  }

  specificObjectiveError(): { [key: string]: any } | null {
    if (!this.saveError?.formErrors?.specificObjectives) {
      return null;
    }
    const args: string[] = [];
    Object.keys(this.saveError?.formErrors?.specificObjectives?.i18nArguments as any).forEach(argKey => args.push((this.saveError?.formErrors?.specificObjectives?.i18nArguments as any)[argKey]));
    return {
      i18nKey: this.saveError?.formErrors?.specificObjectives.i18nKey,
      i8nArguments: {
        arg: args.join(',')
      }
    };
  }

  getCodesByDimension(dimension: string): Map<PartiallyLockableOption, string> {
    if (dimension === 'FormOfSupport') {
      return new Map(Array.from({length: 6}, (x, i) => i + 1)
        .map(i => [{value: String(i).padStart(3, '0'), canBeDeleted: true} as PartiallyLockableOption, String(i).padStart(3, '0')]));
    }
    if (dimension === 'TerritorialDeliveryMechanism') {
      return new Map(Array.from({length: 33}, (x, i) => i + 1)
        .map(i => [{value: String(i).padStart(3, '0'), canBeDeleted: true} as PartiallyLockableOption, String(i).padStart(3, '0')]));
    }
    if (dimension === 'EconomicActivity') {
      return new Map(Array.from({length: 26}, (x, i) => i + 1)
        .map(i => [{value: String(i).padStart(3, '0'), canBeDeleted: true} as PartiallyLockableOption, String(i).padStart(3, '0')]));
    }
    if (dimension === 'GenderEquality') {
      return new Map(Array.from({length: 3}, (x, i) => i + 1)
        .map(i => [{value: String(i).padStart(3, '0'), canBeDeleted: true} as PartiallyLockableOption, String(i).padStart(3, '0')]));
    }
    if (dimension === 'RegionalAndSeaBasinStrategy') {
      return new Map(Array.from({length: 11}, (x, i) => i + 1)
        .map(i => [{value: String(i).padStart(3, '0'), canBeDeleted: true} as PartiallyLockableOption, String(i).padStart(3, '0')]));
    }
    return new Map(Array.from({length: 182}, (x, i) => i + 1)
      .map(i => [{value: String(i).padStart(3, '0'), canBeDeleted: true} as PartiallyLockableOption, String(i).padStart(3, '0')]));
  }

  private addSpecificObjective(objective: ProgrammeSpecificObjectiveDTO, selected: boolean): void {
    const group = this.formBuilder.group({
      selected: this.formBuilder.control(selected),
      code: this.formBuilder.control(selected && objective.code || '', this.constants.POLICY_CODE.validators),
      programmeObjectivePolicy: this.formBuilder.control(objective.programmeObjectivePolicy),
      officialCode: this.formBuilder.control(objective.officialCode)
    });

    if (this.objectivePoliciesAlreadyInUse.find(used => used === objective.programmeObjectivePolicy) || (this.isProgrammeSetupLocked && selected)) {
      group.controls.selected.disable();
      this.form.get(this.constants.OBJECTIVE.name)?.disable();
    }
    group.addControl(this.constants.DIMENSION_CODES.name,
      this.formBuilder.group(this.addDimensionCodes(objective, group, selected))
    );
    group.get(this.constants.POLICY_CODE.name)?.addValidators(this.constants.selectedSpecificObjectiveCodeRequired(group));

    this.specificObjectives.push(group);
  }

  private addDimensionCodes(objective: ProgrammeSpecificObjectiveDTO, group: FormGroup, selected: boolean): { [p: string]: any } {
    return this.dimensions.reduce((a, v) => ({
      ...a,
      [v]: this.formBuilder.control(
        selected && [...(
          objective?.dimensionCodes?.[v]?.map((it) => ({value: it, canBeDeleted: !this.isProgrammeSetupLocked} as PartiallyLockableOption))
          || [])] || [],
        this.constants.dimensionCodesSize(group)
      )}), {});
  }

  private transformSpecificObjectiveValues(policySelectedForm: AbstractControl): any {
    policySelectedForm.value.dimensionCodes = this.dimensions.reduce((a, v) => ({
      ...a,
      [v]: [...policySelectedForm.value.dimensionCodes?.[v].map((it: PartiallyLockableOption) => it.value)] || []
    }), {});
    return policySelectedForm.value;
  }

// TODO: remove when new edit mode is introduced
  private handleSuccess(): void {
    this.saveSuccess = 'programme.priority.save.success';
    this.changeDetectorRef.markForCheck();
    setTimeout(() => {
        this.saveSuccess = '';
        this.changeDetectorRef.markForCheck();
      },
               4000);
  }

  // TODO: remove when new edit mode is introduced
  private handleError(err: HttpErrorResponse, priority: ProgrammePriorityDTO): Observable<ProgrammePriorityDTO> {
    this.saveError = err.error;
    this.changeDetectorRef.markForCheck();
    return of(priority);
  }

  private getAvailableObjectives(currentObjective: string, freeObjectives: { [key: string]: ProgrammeSpecificObjectiveDTO[] }): string[] {
    const objectives = Object.keys(freeObjectives);
    if (!currentObjective || objectives.find(obj => obj === currentObjective)) {
      return objectives;
    }
    return [currentObjective, ...objectives];
  }
}
