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
import {InputProgrammeFund, InputProgrammeFundWrapper, OutputProgrammeFund} from '@cat/api';
import {MatTableDataSource} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';

@Component({
  selector: 'app-programme-basic-funds',
  templateUrl: './programme-basic-funds.component.html',
  styleUrls: ['./programme-basic-funds.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeBasicFundsComponent extends ViewEditForm implements OnInit {
  DEFAULT_FUNDS_LENGTH = 9;

  @Input()
  programmeFunds: InputProgrammeFund[];

  @Output()
  saveFunds = new EventEmitter<InputProgrammeFundWrapper>();

  displayedColumns: string[] = ['select', 'abbreviation', 'description'];
  selection = new SelectionModel<InputProgrammeFund>(true, []);
  dataSource: MatTableDataSource<InputProgrammeFund>;

  editableFundsForm = new FormGroup({});
  // temporary added fund at the end of the table
  lastFund: InputProgrammeFund;

  abbrevErrors = {
    maxlength: 'programme.fund.abbreviation.size.too.long',
    required: 'programme.fund.abbreviation.not.be.empty',
  };

  descErrors = {
    maxlength: 'programme.fund.description.size.too.long'
  }

  constructor(protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource = new MatTableDataSource(this.programmeFunds);
    this.enterViewMode();
  }

  getForm(): FormGroup | null {
    return null;
  }

  addNewFund(): void {
    this.addLastFund();
    this.addControl(this.lastFund);
  }

  onSubmit(): void {
    this.saveFunds.emit({
      funds: this.dataSource.data
        .filter(element => element.id !== this.lastFund.id)
        .map(element => ({
          id: (element.creation ? null : element.id as any),
          selected: this.selection.isSelected(element),
          abbreviation: element.creation ? this.editableFundsForm.get(this.abbreviation(element.id))?.value : null,
          description: element.creation ? this.editableFundsForm.get(this.description(element.id))?.value : null,
          creation: element.creation
        }))
    });
  }

  description = (id: number): string => id + 'desc';
  abbreviation = (id: number): string => id + 'abb';

  isValid(): boolean {
    return Object.keys(this.editableFundsForm.controls)
      .slice(this.DEFAULT_FUNDS_LENGTH * 2, -2) // don't validate the first 9 (default) and last (automatically added) fund
      .every(control => this.editableFundsForm.get(control)?.valid);
  }

  protected enterViewMode(): void {
    this.editableFundsForm = new FormGroup({});
    if (!this.dataSource) {
      return;
    }
    this.dataSource.data = this.programmeFunds;
    this.selection.clear();
    this.selection.select(...this.dataSource.data.filter(element => element.selected));
  }

  protected enterEditMode(): void {
    this.addLastFund();
    this.dataSource.data.forEach(fund => this.addControl(fund));
  }

  private addLastFund(): void {
    this.lastFund = {
      id: this.getNextId(),
      selected: false,
      abbreviation: '',
      description: '',
      creation: true
    };
    this.dataSource.data = [...this.dataSource.data, this.lastFund];
  }

  private addControl(fund: OutputProgrammeFund): void {
    this.editableFundsForm.addControl(
      this.abbreviation(fund.id),
      new FormControl(fund?.abbreviation, [Validators.required, Validators.max(50)])
    );
    this.editableFundsForm.addControl(
      this.description(fund.id),
      new FormControl(fund?.description, Validators.max(250))
    );
  }

  getNextId(): number {
    return Math.max(...this.dataSource.data.map(fund => fund.id)) + 1;
  }

}
