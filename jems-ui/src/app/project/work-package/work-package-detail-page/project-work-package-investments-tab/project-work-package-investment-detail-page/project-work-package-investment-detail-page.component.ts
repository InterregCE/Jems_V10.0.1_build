import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {WorkPackageInvestmentDTO} from '@cat/api';
import {ActivatedRoute, Router} from '@angular/router';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectStore} from '../../../../project-application/containers/project-application-detail/services/project-store.service';
import {catchError, take, tap} from 'rxjs/operators';
import {ProjectWorkPackagePageStore} from '../../project-work-package-page-store.service';
import {NutsStoreService} from '../../../../../common/services/nuts-store.service';
import {ProjectApplicationFormSidenavService} from '../../../../project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import {UntilDestroy} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-project-work-package-investment-detail-page',
  templateUrl: './project-work-package-investment-detail-page.component.html',
  styleUrls: ['./project-work-package-investment-detail-page.component.scss'],
  providers: [FormService, ProjectWorkPackagePageStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectWorkPackageInvestmentDetailPageComponent implements OnInit {

  nuts$ = this.nutsStore.getNuts();

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  workPackageId = this.activatedRoute?.snapshot?.params?.workPackageId;
  workPackageInvestmentId = this.activatedRoute?.snapshot?.params?.workPackageInvestmentId;
  editable: boolean;

  workPackageInvestmentNumber: number;
  investment: WorkPackageInvestmentDTO;

  workPackageInvestmentForm: FormGroup = this.formBuilder.group({
    number: [''],
    title: ['', Validators.maxLength(50)],
    justificationExplanation: ['', Validators.maxLength(2000)],
    justificationTransactionalRelevance: ['', Validators.maxLength(2000)],
    justificationBenefits: ['', Validators.maxLength(2000)],
    justificationPilot: ['', Validators.maxLength(2000)],
    address: this.formBuilder.group({
      country: [''],
      region2: [''],
      region3: [''],
      street: ['', Validators.maxLength(50)],
      houseNumber: ['', Validators.maxLength(20)],
      postalCode: ['', Validators.maxLength(20)],
      city: ['', Validators.maxLength(50)],
      homepage: ['', Validators.maxLength(250)],
    }),
    risk: ['', Validators.maxLength(2000)],
    documentation: ['', Validators.maxLength(2000)],
    ownershipSiteLocation: ['', Validators.maxLength(500)],
    ownershipRetain: ['', Validators.maxLength(500)],
    ownershipMaintenance: ['', Validators.maxLength(2000)],
  });

  titleErrors = {
    maxlength: 'workpackage.title.size.too.long',
  };

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              public projectStore: ProjectStore,
              public workPackageStore: ProjectWorkPackagePageStore,
              public nutsStore: NutsStoreService,
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService) {
    this.projectStore.init(this.projectId);
  }

  ngOnInit(): void {
    this.workPackageInvestmentForm.controls.number.disable();
    if (this.workPackageInvestmentId) {
      this.workPackageStore.getWorkPackageInvestmentById(this.projectId, this.workPackageInvestmentId)
        .pipe(
          tap((investment) => this.investment = investment),
          tap((investment) => this.workPackageInvestmentNumber = investment.investmentNumber)
        );
    }
    this.resetForm();
    this.formService.init(this.workPackageInvestmentForm);
  }

  onCancel(): void {
    if (!this.workPackageInvestmentId) {
      this.redirectToWorkPackageDetail(this.workPackageId);
    }
    this.resetForm();
  }

  onSubmit(): void {
    const workPackageInvestmentFormValues = {
      title: this.workPackageInvestmentForm.controls.title.value,
      justificationExplanation: this.workPackageInvestmentForm.controls.justificationExplanation.value,
      justificationTransactionalRelevance: this.workPackageInvestmentForm.controls.justificationTransactionalRelevance.value,
      justificationBenefits: this.workPackageInvestmentForm.controls.justificationBenefits.value,
      justificationPilot: this.workPackageInvestmentForm.controls.justificationPilot.value,
      address: {
        country: this.address.country.value,
        nutsRegion2: this.address.region2.value,
        nutsRegion3: this.address.region3.value,
        street: this.address.street.value,
        houseNumber: this.address.houseNumber.value,
        postalCode: this.address.postalCode.value,
        city: this.address.city.value
      },
      risk: this.workPackageInvestmentForm.controls.risk.value,
      documentation: this.workPackageInvestmentForm.controls.documentation.value,
      ownershipSiteLocation: this.workPackageInvestmentForm.controls.ownershipSiteLocation.value,
      ownershipRetain: this.workPackageInvestmentForm.controls.ownershipRetain.value,
      ownershipMaintenance: this.workPackageInvestmentForm.controls.ownershipMaintenance.value,
    };
    if (!this.workPackageInvestmentId) {
      const workPackageInvestment = workPackageInvestmentFormValues as WorkPackageInvestmentDTO;

      this.workPackageStore.createWorkPackageInvestment(this.workPackageId, this.projectId, workPackageInvestment)
        .pipe(
          take(1),
          tap(saved => this.redirectToWorkPackageDetail(this.workPackageId)),
          catchError(error => this.formService.setError(error))
        ).subscribe();
      return;

    } else {
      const workPackageInvestment = {
        ...workPackageInvestmentFormValues,
        id: this.workPackageInvestmentId
      } as WorkPackageInvestmentDTO;

      this.workPackageStore.updateWorkPackageInvestment(this.workPackageId, this.projectId, workPackageInvestment)
        .pipe(
          take(1),
          tap(() => this.redirectToWorkPackageDetail(this.workPackageId)),
          catchError(error => this.formService.setError(error))
        ).subscribe();
      return;

    }
  }

  private resetForm(): void {
    this.formService.setEditable(true);
    this.formService.setCreation(!this.workPackageInvestmentId);
    this.workPackageInvestmentForm.controls.number.setValue(this.investment?.investmentNumber || this.workPackageInvestmentNumber);
    this.workPackageInvestmentForm.controls.title.setValue(this.investment?.title);
    this.workPackageInvestmentForm.controls.justificationExplanation.setValue(this.investment?.justificationExplanation);
    this.workPackageInvestmentForm.controls.justificationTransactionalRelevance.setValue(this.investment?.justificationTransactionalRelevance);
    this.workPackageInvestmentForm.controls.justificationBenefits.setValue(this.investment?.justificationBenefits);
    this.workPackageInvestmentForm.controls.justificationPilot.setValue(this.investment?.justificationPilot);
    this.workPackageInvestmentForm.controls.risk.setValue(this.investment?.risk);
    this.workPackageInvestmentForm.controls.documentation.setValue(this.investment?.documentation);
    this.workPackageInvestmentForm.controls.ownershipSiteLocation.setValue(this.investment?.ownershipSiteLocation);
    this.workPackageInvestmentForm.controls.ownershipRetain.setValue(this.investment?.ownershipRetain);
    this.workPackageInvestmentForm.controls.ownershipMaintenance.setValue(this.investment?.ownershipMaintenance);
  }

  get address(): { [key: string]: AbstractControl } {
    return (this.workPackageInvestmentForm.controls?.address as FormGroup).controls;
  }

  private redirectToWorkPackageDetail(workPackageId: number): void {
    this.router.navigate([
      'app', 'project', 'detail', this.projectId, 'applicationFormWorkPackage', 'detail', workPackageId
    ]);
  }
}
