import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Permission} from 'src/app/security/permissions/permission';
import {MatTableDataSource} from '@angular/material/table';
import {ProjectRelevanceBenefit} from '../../dtos/project-relevance-benefit';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {InputProjectRelevanceBenefit} from '@cat/api';
import {Observable} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {BaseComponent} from '@common/components/base-component';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-benefits-table',
  templateUrl: './benefits-table.component.html',
  styleUrls: ['./benefits-table.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BenefitsTableComponent extends BaseComponent implements OnInit {
  Permission = Permission;

  @Input()
  benefitsDataSource: MatTableDataSource<ProjectRelevanceBenefit>;
  @Input()
  editableBenefitsForm = new FormGroup({});
  @Input()
  disabled: boolean
  @Input()
  changedFormState$: Observable<null>;

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
  }

  constructor(private dialog: MatDialog,
              private translateService: TranslateService) {
    super();
  }

  ngOnInit(): void {
    this.changedFormState$
      .pipe(
      takeUntil(this.destroyed$)
    )
      .subscribe(() => {
      this.benefitsDataSource.data.forEach(benefit => this.addControl(benefit));
    })
    this.benefitCounter = this.benefitsDataSource.data.length + 1;
  }

  addNewBenefit(): void {
    this.addControl(this.addLastBenefit());
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
    this.benefitsDataSource.data.splice(index,1);
    this.benefitsDataSource._updateChangeSubscription();
  }
}
