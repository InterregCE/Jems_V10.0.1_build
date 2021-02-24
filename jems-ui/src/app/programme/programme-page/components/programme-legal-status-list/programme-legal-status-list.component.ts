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
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ProgrammeLegalStatusDTO, ProgrammeLegalStatusUpdateDTO} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {Tables} from '../../../../common/utils/tables';
import {UntilDestroy} from '@ngneat/until-destroy';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';

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

  displayedColumns: string[] = ['description', 'delete'];
  dataSource: MatTableDataSource<ProgrammeLegalStatusDTO>;
  toDeleteIds: number[] = [];

  statusForm = new FormGroup({});

  descErrors = {
    maxlength: 'programme.legal.status.description.size.too.long',
    required: 'programme.legal.status.description.not.be.empty',
  };

  constructor(protected changeDetectorRef: ChangeDetectorRef,
              public programmeEditableStateStore: ProgrammeEditableStateStore) {
    super(changeDetectorRef);

    this.programmeEditableStateStore.init();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource = new MatTableDataSource(this.legalStatuses);
    this.enterViewMode();
  }

  getForm(): FormGroup | null {
    return null;
  }

  addNewLegalStatus(): void {
    const legalStatus = {
      id: Tables.getNextId(this.dataSource.data),
      description: [],
    };
    this.dataSource.data = [...this.dataSource.data, legalStatus];
    this.statusForm.addControl(
      String(legalStatus.id),
      new FormControl(legalStatus?.description, [
        Validators.required,
        Validators.maxLength(50)
      ])
    );
  }

  deleteLegalStatus(status: ProgrammeLegalStatusDTO): void {
    this.dataSource.data = this.dataSource.data.filter(element => element.id !== status.id);

    if (this.statusForm.controls[status.id]) {
      this.statusForm.removeControl(String(status.id));
    }

    const persistedStatus = this.legalStatuses.find(element => element.id === status.id);
    if (persistedStatus) {
      this.toDeleteIds.push(persistedStatus.id);
    }
  }

  onSubmit(): void {
    this.saveLegalStatuses.emit({
      toPersist: this.dataSource.data
        .map(element => ({
            id: this.statusForm.controls[element.id] ? null : element.id as any,
            description: this.statusForm.controls[element.id]
              ? this.statusForm.controls[element.id].value : element.description
          })
        ),
      toDeleteIds: this.toDeleteIds
    });
  }

  protected enterViewMode(): void {
    this.statusForm = new FormGroup({});
    if (!this.dataSource) {
      return;
    }
    this.dataSource.data = this.legalStatuses;
  }

  protected enterEditMode(): void {
    this.toDeleteIds = [];
  }
}
