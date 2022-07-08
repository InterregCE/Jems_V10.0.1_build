import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import { FormService } from '@common/components/section/form/form.service';
import {AbstractControl, FormArray, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs';
import {startWith} from 'rxjs/operators';

@Component({
  selector: 'jems-contract-monitoring-extension',
  templateUrl: './contract-monitoring-extension.component.html',
  styleUrls: ['./contract-monitoring-extension.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService]
})
export class ContractMonitoringExtensionComponent {

  decisionForm: FormGroup;
  tableData: AbstractControl[] = [];
  columnsToDisplay = [
    'additionalEntryIntoForceDate',
    'additionalEntryIntoForceComment'
  ];

  isAdditionalDataActivated = false;

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              protected changeDetectorRef: ChangeDetectorRef) {
    this.initForm();
  }

  private initForm(): void {
    this.decisionForm = this.formBuilder.group({
      startDate: [''],
      endDate: [''],
      entryIntoForceDate: [''],
      entryIntoForceComment: ['', Validators.maxLength(200)],
      additionalEntryIntoForceItems: this.formBuilder.array([], Validators.maxLength(10)),
      answer1: [],
      justification1: [],
      answer2: [],
      justification2: [],
      answer3: [],
      justification3: [],
      answer4: [],
      justification4: [],
    });
    this.formService.init(this.decisionForm, new Observable<boolean>().pipe(startWith(true)));
  }

  addAdditionalEntryIntoForceData(): void {
    this.isAdditionalDataActivated = true;
    const item = this.formBuilder.group({
      additionalEntryIntoForceDate: [''],
      additionalEntryIntoForceComment: ['', Validators.maxLength(200)],
    });
    this.additionalEntryIntoForceItems.push(item);
    this.tableData = [...this.additionalEntryIntoForceItems.controls];
    this.formService.setDirty(true);
    setTimeout(() => this.changeDetectorRef.detectChanges());
  }

  get additionalEntryIntoForceItems(): FormArray {
    return this.decisionForm.get('additionalEntryIntoForceItems') as FormArray;
  }

  resetForm(): void {
    this.isAdditionalDataActivated = false;
    this.additionalEntryIntoForceItems.clear();
    this.tableData = [...this.additionalEntryIntoForceItems.controls];
    setTimeout(() => this.changeDetectorRef.detectChanges());
  }

  doSomething(): void {
    this.isAdditionalDataActivated = false;
  }
}
