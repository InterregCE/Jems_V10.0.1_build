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
import {Forms} from '../../../../common/utils/forms';
import {MatDialog} from '@angular/material/dialog';
import {filter, take, tap} from 'rxjs/operators';

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
  @Output()
  deleteStatus = new EventEmitter<number>();

  displayedColumns: string[] = ['add', 'description', 'delete'];
  dataSource: MatTableDataSource<InputProgrammeLegalStatus>;

  statusForm = new FormGroup({});

  descErrors = {
    maxlength: 'programme.legal.status.description.size.too.long',
    required: 'programme.legal.status.description.not.be.empty',
  };

  constructor(protected changeDetectorRef: ChangeDetectorRef,
              private dialog: MatDialog) {
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
      id: this.getNextId(),
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
    Forms.confirmDialog(
      this.dialog,
      'common.dialog.confirm',
      'programme.legal.status.delete.dialog.message',
      {name: status.description}
    ).pipe(
      take(1),
      filter(yes => !!yes),
      tap(() => this.deleteStatus.emit(status.id))
    ).subscribe();
  }

  onSubmit(): void {
    this.saveLegalStatuses.emit({
      statuses: Object.values(this.statusForm.controls)
        .map(element => ({
          id: null as any,
          description: element.value,
        }))
    });
  }

  protected enterViewMode(): void {
    this.statusForm = new FormGroup({});
    if (!this.dataSource) {
      return;
    }
    this.dataSource.data = this.legalStatuses;
  }

  getNextId(): number {
    return Math.max(...this.dataSource.data.map(legalStatus => legalStatus.id)) + 1;
  }
}
