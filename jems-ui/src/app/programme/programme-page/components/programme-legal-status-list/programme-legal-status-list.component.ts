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
import {InputProgrammeLegalStatus, InputProgrammeLegalStatusWrapper} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {Tables} from '../../../../common/utils/tables';

@Component({
  selector: 'app-programme-legal-status-list',
  templateUrl: './programme-legal-status-list.component.html',
  styleUrls: ['./programme-legal-status-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeLegalStatusListComponent extends ViewEditForm implements OnInit {
  @Input()
  legalStatuses: InputProgrammeLegalStatus[];

  @Output()
  saveLegalStatuses = new EventEmitter<InputProgrammeLegalStatusWrapper>();

  displayedColumns: string[] = ['description', 'delete'];
  dataSource: MatTableDataSource<InputProgrammeLegalStatus>;
  toDelete: InputProgrammeLegalStatus[] = [];

  statusForm = new FormGroup({});

  descErrors = {
    maxlength: 'programme.legal.status.description.size.too.long',
    required: 'programme.legal.status.description.not.be.empty',
  };

  constructor(protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
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
      description: '',
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

  deleteLegalStatus(status: InputProgrammeLegalStatus): void {
    this.dataSource.data = this.dataSource.data.filter(element => element.id !== status.id);

    if (this.statusForm.controls[status.id]) {
      this.statusForm.removeControl(String(status.id));
    }

    const persistedStatus = this.legalStatuses.find(element => element.id === status.id);
    if (persistedStatus) {
      this.toDelete.push(persistedStatus);
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
      toDelete: this.toDelete
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
    this.toDelete = [];
  }
}
