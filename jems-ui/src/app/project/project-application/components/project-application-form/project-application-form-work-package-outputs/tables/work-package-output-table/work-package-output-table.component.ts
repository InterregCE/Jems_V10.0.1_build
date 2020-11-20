import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {IndicatorOutputDto, OutputProjectPeriod} from '@cat/api';
import {WorkPackageOutputDetails} from './dto/work-package-output-details';
import {BaseComponent} from '@common/components/base-component';

@Component({
  selector: 'app-work-package-output-table',
  templateUrl: './work-package-output-table.component.html',
  styleUrls: ['./work-package-output-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class WorkPackageOutputTableComponent extends BaseComponent implements OnInit, OnChanges {
  @Input()
  workPackageOutputDataSource: MatTableDataSource<WorkPackageOutputDetails>;
  @Input()
  workPackageForm = new FormGroup({});
  @Input()
  editable: boolean;
  @Input()
  programmeOutputIndicators: IndicatorOutputDto[];
  @Input()
  periods: OutputProjectPeriod[];
  @Input()
  workPackageNumber: number;

  @Output()
  changed = new EventEmitter<void>();

  displayedColumns: string[] = ['output', 'delete'];

  titleErrors = {
    maxlength: 'project.application.form.work.package.output.programme.output.title.too.long'
  };

  targetValueErrors = {
    max: 'project.application.form.work.package.output.programme.output.target.value.too.big',
    min: 'project.application.form.work.package.output.programme.output.target.value.too.big',
  };

  descriptionErrors = {
    maxlength: 'project.application.form.work.package.output.programme.output.description.too.long'
  };

  outputCounter: number;

  ngOnInit(): void {
    if (this.editable &&  this.workPackageOutputDataSource) {
      this.workPackageOutputDataSource.data.forEach(output => this.addControl(output));
    }
    if (this.workPackageOutputDataSource.data) {
      this.outputCounter = this.workPackageOutputDataSource.data.length + 1;
    } else {
      this.outputCounter = 0;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.workPackageForm && this.editable) {
      this.workPackageOutputDataSource.data.forEach(output => this.addControl(output));
    }
  }

  addNewOutput(): void {
    this.addControl(this.addLastOutput());
    this.changed.emit();
  }

  programmeOutputIndicatorId = (id: number): string => id + 'programmeOutputIndicatorId';
  measurementUnit = (id: number): string => id + 'measurementUnit';
  title = (id: number): string => id + 'title';
  targetValue = (id: number): string => id + 'targetValue';
  deliveryPeriod = (id: number): string => id + 'deliveryPeriod';
  description = (id: number): string => id + 'description';

  private addLastOutput(): WorkPackageOutputDetails {
    const lastOutput = {
      id: this.workPackageOutputDataSource.data.length + 1,
      outputNumber: this.outputCounter,
      programmeOutputIndicatorId: 0,
      title: '',
      targetValue: '',
      periodNumber: 0,
      description: '',
      indicator: null
    } as WorkPackageOutputDetails;
    this.workPackageOutputDataSource.data = [...this.workPackageOutputDataSource.data, lastOutput];
    this.outputCounter = this.outputCounter + 1;
    return lastOutput;
  }

  private addControl(output: WorkPackageOutputDetails): void {
    this.workPackageForm.addControl(
      this.programmeOutputIndicatorId(output.outputNumber),
      new FormControl(this.selectCorrectIndicator(output?.programmeOutputIndicatorId), [])
    );
    this.workPackageForm.addControl(
      this.measurementUnit(output.outputNumber),
      new FormControl(output?.indicator ? output?.indicator.measurementUnit : '', [])
    );
    this.workPackageForm.addControl(
      this.title(output.outputNumber),
      new FormControl(output?.title, Validators.maxLength(200))
    );
    this.workPackageForm.addControl(
      this.targetValue(output.outputNumber),
      new FormControl(output?.targetValue, Validators.compose([Validators.max(99999), Validators.min(1)]))
    );
    this.workPackageForm.addControl(
      this.deliveryPeriod(output.outputNumber),
      new FormControl(this.selectCorrectPeriod(output?.periodNumber), [])
    );
    this.workPackageForm.addControl(
      this.description(output.outputNumber),
      new FormControl(output?.description, Validators.maxLength(500))
    );
  }

  deleteEntry(element: WorkPackageOutputDetails): void {
    const index = this.workPackageOutputDataSource.data.indexOf(element);
    this.workPackageOutputDataSource.data.splice(index, 1);
    this.workPackageOutputDataSource.data.forEach((item, position) => {
      item.id = position + 1;
    });
    this.workPackageOutputDataSource._updateChangeSubscription();
    this.changed.emit();
  }

  toggleSelection($event: IndicatorOutputDto, id: number): void {
    this.workPackageForm.controls[this.measurementUnit(id)].setValue($event.measurementUnit);
    this.changed.emit();
  }

  getControl(identifier: any): any {
    return this.workPackageForm?.controls[identifier];
  }

  private selectCorrectPeriod(deliveryPeriod: number): OutputProjectPeriod | undefined {
    return this.periods.find((period) => period.number === deliveryPeriod);
  }

  private selectCorrectIndicator(indicatorId: number): IndicatorOutputDto | undefined {
    return this.programmeOutputIndicators.find((indicator) => indicator.id === indicatorId);
  }
}
