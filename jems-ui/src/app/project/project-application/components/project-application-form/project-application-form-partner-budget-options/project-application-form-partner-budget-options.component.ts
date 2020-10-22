import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input, OnChanges,
  OnInit,
  Output, SimpleChanges
} from '@angular/core';
import {Tools} from '../../../../../common/utils/tools';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {InputFlatRate} from '@cat/api';
import {FormState} from '@common/components/forms/form-state';

@Component({
  selector: 'app-project-application-form-partner-budget-options',
  templateUrl: './project-application-form-partner-budget-options.component.html',
  styleUrls: ['./project-application-form-partner-budget-options.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerBudgetOptionsComponent extends ViewEditForm implements OnInit, OnChanges {
  static ID = 'ProjectApplicationFormPartnerBudgetOptionsComponent';
  ProjectApplicationFormPartnerBudgetOptionsComponent = ProjectApplicationFormPartnerBudgetOptionsComponent;
  Tools = Tools;

  @Input()
  editable: boolean;
  @Input()
  officeAdministrationFlatRate: number | null;

  @Output()
  saveOfficeAdministrationFlatRate = new EventEmitter<InputFlatRate>();

  optionsForm = this.formBuilder.group({
    officeAdministrationFlatRateActive: [''],
    officeAdministrationFlatRate: ['',
      Validators.compose([Validators.max(15), Validators.min(0)])
    ]
  });

  officeAdministrationFlatRateErrors = {
    max: 'project.partner.budget.options.flat.rate.max',
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.discard();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.officeAdministrationFlatRate) {
      this.discard();
    }
  }

  getForm(): FormGroup | null {
    return this.optionsForm;
  }

  save() {
    this.saveOfficeAdministrationFlatRate.emit({
      value: this.optionsForm.controls.officeAdministrationFlatRate.value
    });
  }

  discard() {
    this.optionsForm.patchValue({
      officeAdministrationFlatRateActive: Number.isInteger(this.officeAdministrationFlatRate as any),
      officeAdministrationFlatRate: this.officeAdministrationFlatRate
    });
    this.changeFormState$.next(FormState.VIEW);
  }

  protected enterEditMode() {
    if (!this.optionsForm.controls.officeAdministrationFlatRateActive.value) {
      this.optionsForm.controls.officeAdministrationFlatRate.disable();
    } else {
      this.optionsForm.controls.officeAdministrationFlatRate.enable();
    }
  }

  toggleOfficeAdministrationFlatRate(checked: boolean) {
    this.optionsForm.controls.officeAdministrationFlatRate.patchValue(checked ? 15 : null);
    this.enterEditMode();
  }
}
