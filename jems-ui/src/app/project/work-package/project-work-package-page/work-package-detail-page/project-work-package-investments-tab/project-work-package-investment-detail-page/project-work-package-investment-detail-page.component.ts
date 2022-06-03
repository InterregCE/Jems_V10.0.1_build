import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {OutputNuts, ProjectPeriodDTO, WorkPackageInvestmentDTO} from '@cat/api';
import {ActivatedRoute, Router} from '@angular/router';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, map, take, tap} from 'rxjs/operators';
import {ProjectWorkPackageInvestmentDetailPageConstants} from './project-work-package-investment-detail-page.constants';
import {combineLatest, Observable} from 'rxjs';
import {ProjectWorkPackageInvestmentDetailPageStore} from './project-work-package-Investment-detail-page-store.service';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {WorkPackagePageStore} from '@project/work-package/project-work-package-page/work-package-detail-page/work-package-page-store.service';
import {Alert} from "@common/components/forms/alert";

@Component({
  selector: 'jems-project-work-package-investment-detail-page',
  templateUrl: './project-work-package-investment-detail-page.component.html',
  styleUrls: ['./project-work-package-investment-detail-page.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectWorkPackageInvestmentDetailPageComponent implements OnInit {
  Alert = Alert;

  constants = ProjectWorkPackageInvestmentDetailPageConstants;
  APPLICATION_FORM = APPLICATION_FORM;

  private workPackageInvestmentId: number;

  data$: Observable<{
    investment: WorkPackageInvestmentDTO;
    workPackageNumber: number;
    periods: ProjectPeriodDTO[];
    nuts: OutputNuts[];
  }>;

  workPackageInvestmentForm: FormGroup = this.formBuilder.group({
    number: [''],
    title: ['', this.constants.TITLE.validators],
    expectedDeliveryPeriod: [null],
    justificationExplanation: ['', this.constants.JUSTIFICATION_EXPLANATION.validators],
    justificationTransactionalRelevance: ['', this.constants.JUSTIFICATION_TRANSNATIONAL_RELEVANCE.validators],
    justificationBenefits: ['', this.constants.JUSTIFICATION_BENEFITS.validators],
    justificationPilot: ['', this.constants.JUSTIFICATION_PILOT.validators],
    address: this.formBuilder.group({
      country: [''],
      countryCode: [''],
      region2: [''],
      region2Code: [''],
      region3: [''],
      region3Code: [''],
      street: ['', Validators.maxLength(50)],
      houseNumber: ['', Validators.maxLength(20)],
      postalCode: ['', Validators.maxLength(20)],
      city: ['', Validators.maxLength(50)],
    }),
    risk: ['', this.constants.RISK.validators],
    documentation: ['', this.constants.DOCUMENTATION.validators],
    documentationExpectedImpacts: ['', this.constants.DOCUMENTATION_EXPECTED_IMPACTS.validators],
    ownershipSiteLocation: ['', this.constants.OWNERSHIP_SITE_LOCATION.validators],
    ownershipMaintenance: ['', this.constants.OWNERSHIP_MAINTENANCE.validators],
    ownershipRetain: ['', this.constants.OWNERSHIP_RETAIN.validators],
  });


  constructor(private formBuilder: FormBuilder,
              public formService: FormService,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              public investmentPageStore: ProjectWorkPackageInvestmentDetailPageStore,
              private workPackageStore: WorkPackagePageStore) {
  }

  ngOnInit(): void {
    this.formService.init(this.workPackageInvestmentForm, this.investmentPageStore.isProjectEditable$);

    this.data$ = combineLatest([
      this.investmentPageStore.investment$,
      this.investmentPageStore.workPackageNumber$,
      this.workPackageStore.projectForm$,
      this.investmentPageStore.nuts$
    ]).pipe(
      map(([investment, workPackageNumber, projectForm, nuts]) => ({investment, workPackageNumber, periods : projectForm.periods, nuts})),
      tap(data => this.workPackageInvestmentId = data.investment.id),
      tap(data => this.formService.setCreation(!data.investment.id)),
      tap(data => this.resetForm(data.investment, data.workPackageNumber)),
    );
  }

  onSubmit(): void {
    if (!this.workPackageInvestmentId) {
      this.investmentPageStore.createWorkPackageInvestment(this.workPackageInvestmentForm.value)
        .pipe(
          take(1),
          tap(() => this.redirectToWorkPackageDetail()),
          catchError(error => this.formService.setError(error))
        ).subscribe();
      return;
    }

    const investment = {
      id: this.workPackageInvestmentId,
      ...this.workPackageInvestmentForm.value
    };
    this.investmentPageStore.updateWorkPackageInvestment(investment)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.form.workpackage.investment.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  discard(investment: WorkPackageInvestmentDTO, workPackageNumber: number): void {
    if (!this.workPackageInvestmentId) {
      this.redirectToWorkPackageDetail();
      return;
    }
    this.resetForm(investment, workPackageNumber);
  }

  private resetForm(investment: WorkPackageInvestmentDTO, workPackageNumber: number): void {
    this.workPackageInvestmentForm.controls.number?.setValue(investment?.investmentNumber ? `${workPackageNumber}.${investment?.investmentNumber}` : '');
    this.workPackageInvestmentForm.controls.number.disable();
    this.workPackageInvestmentForm.controls.title?.setValue(investment?.title || []);
    this.workPackageInvestmentForm.controls.justificationExplanation?.setValue(investment?.justificationExplanation || []);
    this.workPackageInvestmentForm.controls.justificationTransactionalRelevance?.setValue(investment?.justificationTransactionalRelevance || []);
    this.workPackageInvestmentForm.controls.justificationBenefits?.setValue(investment?.justificationBenefits || []);
    this.workPackageInvestmentForm.controls.justificationPilot?.setValue(investment?.justificationPilot || []);
    this.address.country.setValue(investment?.address?.country);
    this.address.countryCode.setValue(investment?.address?.countryCode);
    this.address.region2.setValue(investment?.address?.region2);
    this.address.region2Code.setValue(investment?.address?.region2Code);
    this.address.region3.setValue(investment?.address?.region3);
    this.address.region3Code.setValue(investment?.address?.region3Code);
    this.address.street.setValue(investment?.address?.street);
    this.address.houseNumber.setValue(investment?.address?.houseNumber);
    this.address.postalCode.setValue(investment?.address?.postalCode);
    this.address.city.setValue(investment?.address?.city);
    this.workPackageInvestmentForm.controls.risk?.setValue(investment?.risk || []);
    this.workPackageInvestmentForm.controls.documentation?.setValue(investment?.documentation || []);
    this.workPackageInvestmentForm.controls.documentationExpectedImpacts?.setValue(investment?.documentationExpectedImpacts || []);
    this.workPackageInvestmentForm.controls.expectedDeliveryPeriod?.setValue(investment?.expectedDeliveryPeriod || null);
    this.workPackageInvestmentForm.controls.ownershipSiteLocation?.setValue(investment?.ownershipSiteLocation || []);
    this.workPackageInvestmentForm.controls.ownershipMaintenance?.setValue(investment?.ownershipMaintenance || []);
    this.workPackageInvestmentForm.controls.ownershipRetain?.setValue(investment?.ownershipRetain || []);
  }

  get address(): { [key: string]: AbstractControl } {
    return (this.workPackageInvestmentForm.controls?.address as FormGroup).controls;
  }

  private redirectToWorkPackageDetail(): void {
    this.router.navigate(['..'], {relativeTo: this.activatedRoute});
  }
}
