import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {UntilDestroy} from '@ngneat/until-destroy';
import {Log} from '@common/utils/log';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {combineLatest, Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import {ProjectPeriodDTO} from '@cat/api';
import {ContractReportingStore} from '@project/project-application/contract-reporting/contract-reporting.store';

@UntilDestroy()
@Component({
  selector: 'jems-contract-reporting',
  templateUrl: './contract-reporting.component.html',
  styleUrls: ['./contract-reporting.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class ContractReportingComponent implements OnInit {
  reportingDeadlinesForm: FormGroup;
  tableData: AbstractControl[] = [];
  columnsToDisplay = [
    'deadlineReportType',
    'deadlinePeriod',
    'deadlineDate',
    'deadlineComment'
  ];
  data$: Observable<{
    periods: ProjectPeriodDTO[];
  }>;

  constructor(private formBuilder: FormBuilder,
              private contractReportingStore: ContractReportingStore,
              public formService: FormService) {
    this.initForm();
  }

  ngOnInit(): void {
    this.data$ = combineLatest([
      this.contractReportingStore.projectForm$,
    ]).pipe(
      map(([projectForm]) => ({
          periods: projectForm.periods,
        })
      ));
  }

  addDeadlineData(): void {
    const item = this.formBuilder.group({
      deadlineReportType: [''],
      deadlinePeriod: [''],
      deadlineDate: [''],
      deadlineComment: ['', Validators.maxLength(1000)],
    });
    this.deadlines.push(item);
    this.tableData = [...this.deadlines.controls];
    this.formService.setDirty(true);
  }

  get deadlines(): FormArray {
    return this.reportingDeadlinesForm.get('deadlines') as FormArray;
  }

  resetForm() {
    Log.info('Reset pressed');
  }

  onSubmit() {
    Log.info('Onsubmit pressed', this.deadlines);
  }

  private initForm(): void {
    this.reportingDeadlinesForm = this.formBuilder.group({
      deadlines: this.formBuilder.array([], Validators.maxLength(50)),
    });
    this.formService.init(this.reportingDeadlinesForm, new Observable<boolean>().pipe(startWith(true)));
  }
}
