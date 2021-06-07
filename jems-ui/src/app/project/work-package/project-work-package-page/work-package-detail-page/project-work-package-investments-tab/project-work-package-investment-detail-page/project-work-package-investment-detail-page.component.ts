import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {WorkPackageInvestmentDTO} from '@cat/api';
import {ActivatedRoute, Router} from '@angular/router';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {catchError, take, tap, withLatestFrom} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProjectWorkPackageInvestmentDetailPageConstants} from './project-work-package-investment-detail-page.constants';
import {Observable} from 'rxjs';
import {ProjectWorkPackageInvestmentDetailPageStore} from './project-work-package-Investment-detail-page-store.service';
import {ProjectWorkPackagePageStore} from '../../project-work-package-page-store.service';
import {filter} from 'rxjs/internal/operators';
import {NutsStore} from '../../../../../../common/services/nuts.store';

@UntilDestroy()
@Component({
  selector: 'app-project-work-package-investment-detail-page',
  templateUrl: './project-work-package-investment-detail-page.component.html',
  styleUrls: ['./project-work-package-investment-detail-page.component.scss'],
  providers: [FormService, ProjectWorkPackageInvestmentDetailPageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectWorkPackageInvestmentDetailPageComponent implements OnInit {
  constants = ProjectWorkPackageInvestmentDetailPageConstants;

  private projectId = this.activatedRoute?.snapshot?.params?.projectId;
  private workPackageId = this.activatedRoute?.snapshot?.params?.workPackageId;
  private workPackageInvestmentId = this.activatedRoute?.snapshot?.params?.workPackageInvestmentId;
  workPackageNumber: number;

  nuts$ = this.nutsStore.getNuts();
  workPackageInvestment$: Observable<WorkPackageInvestmentDTO>;

  workPackageInvestmentForm: FormGroup = this.formBuilder.group({
    number: [''],
    title: ['', this.constants.TITLE.validators],
    justificationExplanation: ['', this.constants.JUSTIFICATION_EXPLANATION.validators],
    justificationTransactionalRelevance: ['', this.constants.JUSTIFICATION_TRANSNATIONAL_RELEVANCE.validators],
    justificationBenefits: ['', this.constants.JUSTIFICATION_BENEFITS.validators],
    justificationPilot: ['', this.constants.JUSTIFICATION_PILOT.validators],
    address: this.formBuilder.group({
      country: [''],
      region2: [''],
      region3: [''],
      street: ['', Validators.maxLength(50)],
      houseNumber: ['', Validators.maxLength(20)],
      postalCode: ['', Validators.maxLength(20)],
      city: ['', Validators.maxLength(50)],
    }),
    risk: ['', this.constants.RISK.validators],
    documentation: ['', this.constants.DOCUMENTATION.validators],
    ownershipSiteLocation: ['', this.constants.OWNERSHIP_SITE_LOCATION.validators],
    ownershipMaintenance: ['', this.constants.OWNERSHIP_MAINTENANCE.validators],
    ownershipRetain: ['', this.constants.OWNERSHIP_RETAIN.validators],
  });


  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              public investmentPageStore: ProjectWorkPackageInvestmentDetailPageStore,
              public workPackageStore: ProjectWorkPackagePageStore,
              public nutsStore: NutsStore) {
  }

  ngOnInit(): void {
    this.formService.init(this.workPackageInvestmentForm);
    this.formService.setCreation(!this.workPackageInvestmentId);

    this.investmentPageStore.isProjectEditable$
      .pipe(
        take(1),
        tap(editable => this.formService.setEditable(editable)),
        tap(() => this.workPackageInvestmentForm.controls.number.disable())
      ).subscribe();

    this.workPackageInvestment$ = this.investmentPageStore.workPackageInvestment(this.workPackageInvestmentId, this.workPackageId)
      .pipe(
        tap(investment => this.resetForm(investment)),
      );

    this.workPackageStore.workPackage$
      .pipe(
        filter(workPackage => !!workPackage?.number),
        tap(workPackage => this.workPackageNumber = workPackage.number),
        untilDestroyed(this)
      ).subscribe();

    this.formService.reset$
      .pipe(
        withLatestFrom(this.workPackageInvestment$),
        tap(([reset, investment]) => {
          if (this.workPackageInvestmentId) {
            this.resetForm(investment);
            return;
          }
          this.redirectToWorkPackageDetail();
        }),
        untilDestroyed(this)
      ).subscribe();
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

  private resetForm(investment: WorkPackageInvestmentDTO): void {
    this.workPackageInvestmentForm.controls.number?.setValue(investment?.investmentNumber ? `${this.workPackageNumber}.${investment?.investmentNumber}` : '');
    this.workPackageInvestmentForm.controls.title?.setValue(investment?.title || []);
    this.workPackageInvestmentForm.controls.justificationExplanation?.setValue(investment?.justificationExplanation || []);
    this.workPackageInvestmentForm.controls.justificationTransactionalRelevance?.setValue(investment?.justificationTransactionalRelevance || []);
    this.workPackageInvestmentForm.controls.justificationBenefits?.setValue(investment?.justificationBenefits || []);
    this.workPackageInvestmentForm.controls.justificationPilot?.setValue(investment?.justificationPilot || []);
    this.address.country.setValue(investment?.address?.country);
    this.address.region2.setValue(investment?.address?.region2);
    this.address.region3.setValue(investment?.address?.region3);
    this.address.street.setValue(investment?.address?.street);
    this.address.houseNumber.setValue(investment?.address?.houseNumber);
    this.address.postalCode.setValue(investment?.address?.postalCode);
    this.address.city.setValue(investment?.address?.city);
    this.workPackageInvestmentForm.controls.risk?.setValue(investment?.risk || []);
    this.workPackageInvestmentForm.controls.documentation?.setValue(investment?.documentation || []);
    this.workPackageInvestmentForm.controls.ownershipSiteLocation?.setValue(investment?.ownershipSiteLocation || []);
    this.workPackageInvestmentForm.controls.ownershipMaintenance?.setValue(investment?.ownershipMaintenance || []);
    this.workPackageInvestmentForm.controls.ownershipRetain?.setValue(investment?.ownershipRetain || []);
  }

  get address(): { [key: string]: AbstractControl } {
    return (this.workPackageInvestmentForm.controls?.address as FormGroup).controls;
  }

  private redirectToWorkPackageDetail(): void {
    this.router.navigate([
      'app', 'project', 'detail', this.projectId, 'applicationFormWorkPackage', 'detail', this.workPackageId
    ]);
  }
}
