import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {ChecklistComponentInstanceDTO, ProgrammeChecklistComponentDTO, ProgrammeChecklistDetailDTO} from '@cat/api';
import {FormArray, FormBuilder, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';
import {FormService} from '@common/components/section/form/form.service';
import {RoutingService} from '@common/services/routing.service';
import {ActivatedRoute} from '@angular/router';
import {TableConfig} from '@common/directives/table-config/TableConfig';
import {ProgrammeChecklistDetailPageStore} from './programme-checklist-detail-page-store.service';
import {ProgrammePageSidenavService} from '../../programme-page/services/programme-page-sidenav.service';
import {Alert} from '@common/components/forms/alert';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'jems-programme-checklist-detail-page',
  templateUrl: './programme-checklist-detail-page.component.html',
  styleUrls: ['./programme-checklist-detail-page.component.scss'],
  providers: [ProgrammeChecklistDetailPageStore, FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeChecklistDetailPageComponent implements OnInit {
  CHECKLIST_TYPE = ProgrammeChecklistDetailDTO.TypeEnum;
  COMPONENT_TYPE = ProgrammeChecklistComponentDTO.TypeEnum;
  Alert = Alert;

  FORM_ERRORS = {
    minScore: {
      max: 'programme.checklists.detail.range.min.error'
    },
    maxScore: {
      min: 'programme.checklists.detail.range.max.error'
    }
  };
  data$: Observable<{
    checklist: ProgrammeChecklistDetailDTO;
    previewComponents: ChecklistComponentInstanceDTO[];
  }>;

  form = this.formBuilder.group({
    id: [null],
    type: ['', Validators.required],
    name: ['', Validators.maxLength(100)],
    minScore: ['0', [Validators.required, Validators.min(0), Validators.max(10)]],
    maxScore: ['10', [Validators.required, Validators.min(0), Validators.max(100)]],
    allowsDecimalScore: [false],
    components: this.formBuilder.array([])
  });

  constructor(private programmePageSidenavService: ProgrammePageSidenavService,
              public pageStore: ProgrammeChecklistDetailPageStore,
              private formBuilder: FormBuilder,
              public formService: FormService,
              private routingService: RoutingService,
              private activatedRoute: ActivatedRoute) {
    this.data$ = this.pageStore.checklist$
      .pipe(
        tap(checklist => this.resetForm(checklist)),
        tap(checklist => this.formService.setCreation(!checklist?.id)),
        map(checklist => ({
          checklist,
          previewComponents: this.getPreviewComponents(checklist.components)
        }))
      );
  }

  ngOnInit() {
    this.formService.init(this.form, this.pageStore.isEditable$);
    const minControl = this.form.get('minScore');
    const maxControl = this.form.get('maxScore');
    minControl?.setValidators([Validators.required, Validators.max(this.form.value?.maxScore || 10)]);
    maxControl?.setValidators([Validators.required, Validators.min(this.form.value?.minScore || 0), Validators.max(100)]);
    minControl?.valueChanges.pipe(untilDestroyed(this)).subscribe(value => {
      if (value !== this.form.value?.minScore) {
        maxControl?.setValidators([Validators.required, Validators.min(value), Validators.max(100)]);
        maxControl?.updateValueAndValidity({emitEvent: false});
      }
    });
    maxControl?.valueChanges.pipe(untilDestroyed(this)).subscribe(value => {
      if (value !== this.form.value?.maxScore) {
        minControl?.setValidators([Validators.required, Validators.max(value)]);
        minControl?.updateValueAndValidity({emitEvent: false});
      }
    });
  }

  save(): void {
    this.updateScoreValues();
    this.components.controls.forEach(
      (component, index) => component.get('position')?.setValue(index)
    );
    this.pageStore.saveChecklist(this.form.value)
      .pipe(
        tap(() => this.formService.setSuccess('programme.checklists.saved.successfully')),
        catchError(error => this.formService.setError(error))
      )
      .subscribe();
  }


  discard(checklist: ProgrammeChecklistDetailDTO): void {
    if (!checklist?.id) {
      this.routingService.navigate(['..'], {relativeTo: this.activatedRoute});
      return;
    }
    this.resetForm(checklist);
  }

  addComponent(component?: ProgrammeChecklistComponentDTO): void {
    this.components.push(this.formBuilder.group({
      id: [component?.id],
      type: [component?.type, Validators.required],
      position: [component?.position],
      metadata: [component?.metadata],
    }));
  }

  move(shift: number, componentIndex: number): void {
    const controls = this.components.controls;
    [controls[componentIndex], controls[componentIndex + shift]] = [controls[componentIndex + shift], controls[componentIndex]];
    this.formService.setDirty(true);
  }

  componentTypeChanged(): void {
    // when type changes the component is changed and the validity check is not performed
    setTimeout(() => this.form.updateValueAndValidity(), 100);
  }

  getTableConfig(): TableConfig[] {
    return [...this.form.enabled ? [{maxInRem: 3}] : [], {maxInRem: 15}, {}, {maxInRem: 3}];
  }

  get components(): FormArray {
    return this.form.get('components') as FormArray;
  }

  get checklistTypes(): ProgrammeChecklistDetailDTO.TypeEnum[] {
    return Object.values(this.CHECKLIST_TYPE);
  }

  get componentTypes(): ProgrammeChecklistComponentDTO.TypeEnum[] {
    return Object.values(this.COMPONENT_TYPE);
  }

  private resetForm(checklist: ProgrammeChecklistDetailDTO) {
    this.form.patchValue(checklist);
    this.components.clear();
    checklist?.components?.forEach(component => this.addComponent(component));
    this.formService.resetEditable();
  }

  private getPreviewComponents(components: ProgrammeChecklistComponentDTO[]): ChecklistComponentInstanceDTO[] {
    return components ? components.map(component => ({
      id: null as any,
      type: component.type ,
      position: component.position,
      programmeMetadata: component.metadata,
      instanceMetadata: null as any
    })) : [];
  }

  updateScoreValues() {
    if (!this.form.value?.allowsDecimalScore) {
      this.form.get('minScore')?.setValue(parseInt(this.form.get('minScore')?.value, 10));
      this.form.get('maxScore')?.setValue(parseInt(this.form.get('maxScore')?.value, 10));
    }
  }
}
