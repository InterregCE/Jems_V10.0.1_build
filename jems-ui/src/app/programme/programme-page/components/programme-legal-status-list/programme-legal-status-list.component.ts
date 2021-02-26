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
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {ProgrammeLegalStatusDTO, ProgrammeLegalStatusUpdateDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {Tables} from '../../../../common/utils/tables';
import {UntilDestroy} from '@ngneat/until-destroy';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';
import {FormState} from '@common/components/forms/form-state';

@UntilDestroy()
@Component({
  selector: 'app-programme-legal-status-list',
  templateUrl: './programme-legal-status-list.component.html',
  styleUrls: ['./programme-legal-status-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeLegalStatusListComponent extends ViewEditForm implements OnInit {
  @Input()
  legalStatuses: ProgrammeLegalStatusDTO[];

  @Output()
  saveLegalStatuses = new EventEmitter<ProgrammeLegalStatusUpdateDTO>();

  isProgrammeSetupRestricted = false;
  toDeleteIds: number[] = [];

  statusForm = new FormGroup({});

  constructor(protected changeDetectorRef: ChangeDetectorRef,
              private formBuilder: FormBuilder,
              public programmeEditableStateStore: ProgrammeEditableStateStore) {
    super(changeDetectorRef);
    this.programmeEditableStateStore.init();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.statusForm = this.formBuilder.group({
      legalStatuses: this.formBuilder.array([]),
    });
    this.resetForm();
  }

  get legalStatusesForm(): FormArray {
    return this.statusForm.get('legalStatuses') as FormArray;
  }

  getForm(): FormGroup | null {
    return this.statusForm;
  }

  resetForm(): void {
    this.legalStatusesForm.clear();
    this.legalStatuses.forEach(legalStatus => this.addControl(legalStatus));
    this.changeFormState$.next(FormState.VIEW);
  }

  addNewLegalStatus(): void {
    this.addControl();
  }

  addControl(legalStatus?: ProgrammeLegalStatusDTO): void {
    this.legalStatusesForm.push(this.formBuilder.group({
      id: this.formBuilder.control(legalStatus?.id),
      description: this.formBuilder.control(legalStatus?.description || []),
    }));
  }

  deleteLegalStatus(elementIndex: number, elementId?: number): void {
    this.legalStatusesForm.removeAt(elementIndex);
    if (elementId) {
      this.toDeleteIds.push(elementId);
    }
  }

  onSubmit(): void {
    this.saveLegalStatuses.emit({
      toPersist: this.statusForm.controls.legalStatuses.value
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
}
