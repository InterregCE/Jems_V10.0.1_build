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
import {Permission} from 'src/app/security/permissions/permission';
import {MatTableDataSource} from '@angular/material/table';
import {ProjectRelevanceBenefit} from '../../dtos/project-relevance-benefit';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {InputProjectRelevanceBenefit} from '@cat/api';

@Component({
  selector: 'app-benefits-table',
  templateUrl: './benefits-table.component.html',
  styleUrls: ['./benefits-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BenefitsTableComponent implements OnInit, OnChanges {
  Permission = Permission;

  @Input()
  benefitsDataSource: MatTableDataSource<ProjectRelevanceBenefit>;
  @Input()
  editableBenefitsForm = new FormGroup({});
  @Input()
  editable: boolean;

  @Output()
  changed = new EventEmitter<void>();

  displayedColumns: string[] = ['select', 'targetGroup', 'specification', 'delete'];

  benefitCounter: number;
  benefitEnums = [
    'LocalPublicAuthority',
    'RegionalPublicAuthority',
    'NationalPublicAuthority',
    'SectoralAgency',
    'InfrastructureAndServiceProvider',
    'InterestGroups',
    'HigherEducationOrganisations',
    'EducationTrainingCentreAndSchool',
    'EnterpriseExceptSme',
    'Sme',
    'BusinessSupportOrganisation',
    'Egtc',
    'InternationalOrganisationEeig',
    'GeneralPublic',
    'Hospitals',
    'Other'];

  targetGroupErrors = {
    required: 'project.application.form.relevance.target.group.not.empty',
  };
  specificationErrors = {
    maxlength: 'project.application.form.relevance.specification.size.too.long'
  };

  ngOnInit(): void {
    if (this.editable) {
      this.benefitsDataSource.data.forEach(benefit => this.addControl(benefit));
    }
    this.benefitCounter = this.benefitsDataSource.data.length + 1;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.editableBenefitsForm && this.editable) {
      this.benefitsDataSource.data.forEach(benefit => this.addControl(benefit));
    }
  }

  addNewBenefit(): void {
    this.addControl(this.addLastBenefit());
    this.changed.emit();
  }

  targetGroup = (id: number): string => id + 'targ';
  specification = (id: number): string => id + 'spec';

  isValid(): boolean {
    return Object.keys(this.editableBenefitsForm.controls)
      .every(control => this.editableBenefitsForm.get(control)?.valid);
  }

  private addLastBenefit(): ProjectRelevanceBenefit {
    const lastBenefit = {
      id: this.benefitCounter,
      targetGroup: InputProjectRelevanceBenefit.GroupEnum.Other,
      specification: ''
    } as ProjectRelevanceBenefit;
    this.benefitsDataSource.data = [...this.benefitsDataSource.data, lastBenefit];
    this.benefitCounter = this.benefitCounter + 1;
    return lastBenefit;
  }

  private addControl(benefit: ProjectRelevanceBenefit): void {
    this.editableBenefitsForm.addControl(
      this.targetGroup(benefit.id),
      new FormControl(benefit?.targetGroup, [])
    );
    this.editableBenefitsForm.addControl(
      this.specification(benefit.id),
      new FormControl(benefit?.specification, Validators.maxLength(2000))
    );
  }

  deleteEntry(element: ProjectRelevanceBenefit): void {
    const index = this.benefitsDataSource.data.indexOf(element);
    this.benefitsDataSource.data.splice(index, 1);
    this.benefitsDataSource._updateChangeSubscription();
    this.changed.emit();
  }
}
