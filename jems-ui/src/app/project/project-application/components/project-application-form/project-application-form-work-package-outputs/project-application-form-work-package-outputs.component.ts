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
import {BaseComponent} from '@common/components/base-component';
import {Observable} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {OutputWorkPackage} from '@cat/api';
import {FormBuilder, FormGroup} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {takeUntil, tap} from 'rxjs/operators';
import {MatTableDataSource} from '@angular/material/table';
import {WorkPackageOutputUpdateDTO, WorkPackageOutputDTO, OutputProjectPeriod, IndicatorOutputDto} from '@cat/api';
import {ProjectStore} from '../../../containers/project-application-detail/services/project-store.service';
import {WorkPackageOutputDetails} from './tables/work-package-output-table/dto/work-package-output-details';

@Component({
  selector: 'app-project-application-form-work-package-outputs',
  templateUrl: './project-application-form-work-package-outputs.component.html',
  styleUrls: ['./project-application-form-work-package-outputs.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormWorkPackageOutputsComponent extends BaseComponent implements OnInit, OnChanges {

  // TODO: remove these and adapt the component to save independently
  @Input()
  error$: Observable<HttpErrorResponse | null>;
  @Input()
  success$: Observable<any>;

  @Input()
  workPackage: OutputWorkPackage;
  @Input()
  editable: boolean;
  @Input()
  projectId: number;
  @Input()
  workPackageOutputs: WorkPackageOutputDTO[];
  @Input()
  periods: OutputProjectPeriod[];
  @Input()
  indicators: IndicatorOutputDto[];
  @Output()
  updateData = new EventEmitter<WorkPackageOutputUpdateDTO[]>();
  @Output()
  cancel = new EventEmitter<void>();

  workPackageNumber: number;
  workPackageOutputDataSource: MatTableDataSource<WorkPackageOutputDetails>;
  outputCounter: number;
  workPackageForm: FormGroup = this.formBuilder.group({
  });

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private projectStore: ProjectStore) {
    super();
    this.projectStore.init(this.projectId);
  }

  programmeOutputIndicatorId = (id: number): string => id + 'programmeOutputIndicatorId';
  measurementUnit = (id: number): string => id + 'measurementUnit';
  title = (id: number): string => id + 'title';
  targetValue = (id: number): string => id + 'targetValue';
  deliveryPeriod = (id: number): string => id + 'deliveryPeriod';
  description = (id: number): string => id + 'description';

  ngOnInit(): void {
    this.workPackageNumber = this.workPackage?.number;
    this.workPackageOutputDataSource = new MatTableDataSource<WorkPackageOutputDetails>(this.constructWorkPackageOutputsDataSource());
    this.resetForm();

    this.formService.init(this.workPackageForm);
    this.formService.setCreation(!this.workPackage.id);
    this.error$
      .pipe(
        takeUntil(this.destroyed$),
        tap(err => this.formService.setError(err))
      )
      .subscribe();
    this.success$
      .pipe(
        takeUntil(this.destroyed$),
        tap(() => this.formService.setSuccess('project.application.form.workpackage.outputs.save.success'))
      )
      .subscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.workPackageOutputs) {
      this.resetForm();
      this.workPackageNumber = this.workPackage?.number;
    }
  }

  onSubmit(): void {
    this.updateData.emit(this.buildOutputsToSave());
  }

  onCancel(): void {
    if (!this.workPackage.id) {
      this.cancel.emit();
    }
    this.resetForm();
  }

  tableChanged(): void {
    this.formService.setDirty(true);
    this.formService.setValid(this.workPackageForm.valid);
  }

  private resetForm(): void {
    if (this.workPackageOutputDataSource) {
      this.workPackageOutputDataSource.data = this.constructWorkPackageOutputsDataSource();
    } else {
      this.workPackageOutputDataSource = new MatTableDataSource<WorkPackageOutputDetails>();
    }
    this.workPackageForm = new FormGroup({});
  }

  private constructWorkPackageOutputsDataSource(): WorkPackageOutputDetails[] {
    const data: WorkPackageOutputDetails[] = [];
    this.outputCounter = 1;
    this.workPackageOutputs.forEach((element) => {
      data.push({
        id: element.outputNumber,
        outputNumber: element.outputNumber,
        programmeOutputIndicatorId: element.programmeOutputIndicator?.id,
        title: element.title,
        targetValue: element.targetValue,
        periodNumber: element.periodNumber,
        description: element.description,
        indicator: element.programmeOutputIndicator,
      } as WorkPackageOutputDetails);
      this.outputCounter = this.outputCounter + 1;
    });
    return data;
  }

  private buildOutputsToSave(): WorkPackageOutputUpdateDTO[] {
    return this.workPackageOutputDataSource.data
      .map(element => ({
        outputNumber: element.id,
        programmeOutputIndicatorId: this.workPackageForm.get(this.programmeOutputIndicatorId(element.outputNumber))?.value?.id,
        title: this.workPackageForm.get(this.title(element.outputNumber))?.value,
        targetValue: this.workPackageForm.get(this.targetValue(element.outputNumber))?.value,
        periodNumber: this.workPackageForm.get(this.deliveryPeriod(element.outputNumber))?.value?.number,
        description: this.workPackageForm.get(this.description(element.outputNumber))?.value
      }));
  }
}
