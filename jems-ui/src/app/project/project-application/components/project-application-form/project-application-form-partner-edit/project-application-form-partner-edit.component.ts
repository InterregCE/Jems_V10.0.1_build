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
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {
  InputProjectPartnerCreate,
  InputProjectPartnerUpdate,
  InputTranslation,
  OutputProjectPartner,
  OutputProjectPartnerDetail,
  ProgrammeLegalStatusDTO,
} from '@cat/api';
import {catchError, take, takeUntil, tap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {Forms} from '../../../../../common/utils/forms';
import {FormService} from '@common/components/section/form/form.service';
import {BaseComponent} from '@common/components/base-component';
import {ProjectPartnerStore} from '../../../containers/project-application-form-page/services/project-partner-store.service';
import {HttpErrorResponse} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {APIError} from '../../../../../common/models/APIError';

@Component({
  selector: 'app-project-application-form-partner-edit',
  templateUrl: './project-application-form-partner-edit.component.html',
  styleUrls: ['./project-application-form-partner-edit.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerEditComponent extends BaseComponent implements OnInit, OnChanges {
  RoleEnum = OutputProjectPartner.RoleEnum;
  VatRecoveryEnum = InputProjectPartnerCreate.VatRecoveryEnum;
  LANGUAGE = InputTranslation.LanguageEnum;

  partnerId = this.activatedRoute?.snapshot?.params?.partnerId;

  @Input()
  partner: OutputProjectPartnerDetail;
  @Input()
  projectId: number;
  @Input()
  legalStatuses: ProgrammeLegalStatusDTO[];

  @Output()
  create = new EventEmitter<InputProjectPartnerCreate>();
  @Output()
  update = new EventEmitter<InputProjectPartnerUpdate>();
  @Output()
  cancel = new EventEmitter<void>();

  partnerForm: FormGroup = this.formBuilder.group({
    fakeRole: [], // needed for the fake role field in view mode
    id: [],
    sortNumber: [],
    abbreviation: ['', [
      Validators.maxLength(15),
      Validators.required]
    ],
    role: ['', Validators.required],
    nameInOriginalLanguage: ['', Validators.maxLength(100)],
    nameInEnglish: [[], Validators.maxLength(100)],
    department: [],
    partnerType: [''],
    legalStatusId: ['', Validators.required],
    vat: ['', Validators.maxLength(50)],
    vatRecovery: ['']
  });

  roleErrors = {
    required: 'project.partner.role.should.not.be.empty',
  };
  legalStatusErrors = {
    required: 'project.partner.legal.status.should.not.be.empty'
  };

  partnerTypeEnums = [
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

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              public formService: FormService,
              private partnerStore: ProjectPartnerStore,
              private router: Router,
              private activatedRoute: ActivatedRoute) {
    super();
  }

  ngOnInit(): void {
    this.formService.init(this.partnerForm, this.partnerStore.isProjectEditable$);
    this.resetForm();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.partner || changes.editable) {
      this.resetForm();
    }
  }

  get controls(): any {
    return this.partnerForm.controls;
  }

  onSubmit(controls: any, oldPartnerId?: number): void {
    if (!controls.id?.value) {
      const partnerToCreate = {
        abbreviation: this.controls.abbreviation.value,
        role: this.controls.role.value,
        oldLeadPartnerId: oldPartnerId,
        nameInOriginalLanguage: this.controls.nameInOriginalLanguage.value,
        nameInEnglish: this.controls.nameInEnglish.value[0].translation,
        department: this.controls.department.value,
        partnerType: this.controls.partnerType.value,
        legalStatusId: this.controls.legalStatusId.value,
        vat: this.controls.vat.value,
        vatRecovery: this.controls.vatRecovery.value,
      };

      partnerToCreate.oldLeadPartnerId = oldPartnerId;
      if (!controls.partnerType.value) {
        partnerToCreate.partnerType = null;
      }

      this.partnerStore.createPartner(partnerToCreate as InputProjectPartnerCreate)
        .pipe(
          take(1),
          tap(created => this.redirectToPartnerDetail(created)),
          catchError(error => this.handleError(error))
        ).subscribe();
    } else {
      const partnerToUpdate = {
        id: this.partner.id,
        abbreviation: this.controls.abbreviation.value,
        role: this.controls.role.value,
        oldLeadPartnerId: oldPartnerId,
        nameInOriginalLanguage: this.controls.nameInOriginalLanguage.value,
        nameInEnglish: this.controls.nameInEnglish.value[0].translation,
        department: this.controls.department.value,
        partnerType: this.controls.partnerType.value,
        legalStatusId: this.controls.legalStatusId.value,
        vat: this.controls.vat.value,
        vatRecovery: this.controls.vatRecovery.value,
      };

      partnerToUpdate.oldLeadPartnerId = oldPartnerId;
      if (!controls.partnerType.value) {
        partnerToUpdate.partnerType = null;
      }
      this.partnerStore.savePartner(partnerToUpdate as InputProjectPartnerUpdate)
        .pipe(
          take(1),
          tap(() => this.formService.setSuccess('project.partner.save.success')),
          catchError(error => this.handleError(error))
        ).subscribe();
    }
  }

  setRole(role: OutputProjectPartner.RoleEnum): void {
    this.controls?.role.setValue(role);
    this.formService.setDirty(true);
  }

  setVat(vat: InputProjectPartnerCreate.VatRecoveryEnum): void {
    this.controls?.vatRecovery.setValue(vat);
    this.formService.setDirty(true);
  }

  discard(): void {
    if (!this.partner?.id) {
      this.redirectToPartnerOverview();
    } else {
      this.resetForm();
    }
  }

  private handleError(error: HttpErrorResponse): Observable<any> {
    if (!!error && error.error?.i18nMessage?.i18nKey === 'project.partner.role.lead.already.existing') {
      this.handleLeadAlreadyExisting(this.controls, error.error as APIError);
      return of(null);
    }
    return this.formService.setError(error);
  }

  private resetForm(): void {
    if (!this.partnerId) {
      this.formService.setCreation(true);
    }
    this.controls.id.setValue(this.partner?.id);
    this.controls.abbreviation.setValue(this.partner?.abbreviation);
    this.controls.role.setValue(this.partner?.role);
    this.controls.nameInOriginalLanguage.setValue(this.partner?.nameInOriginalLanguage);
    this.controls.nameInEnglish.setValue([{
      language: this.LANGUAGE.EN,
      translation: this.partner?.nameInEnglish || ''
    }]);
    this.controls.department.setValue(this.partner?.department);
    this.controls.partnerType.setValue(this.partner?.partnerType);
    this.controls.legalStatusId.setValue(this.partner?.legalStatusId);
    this.controls.vat.setValue(this.partner?.vat);
    this.controls.vatRecovery.setValue(this.partner?.vatRecovery);
    this.controls.sortNumber.setValue(this.partner?.sortNumber);
  }

  private handleLeadAlreadyExisting(controls: any, error: APIError): void {
    const oldLeadPartnerName = error.i18nMessage.i18nArguments ? error.i18nMessage.i18nArguments[1] : null;
    const partnerId = error.i18nMessage.i18nArguments ? error.i18nMessage.i18nArguments[0] : null;
    Forms.confirmDialog(
      this.dialog,
      'project.partner.role.lead.already.existing.title',
      'project.partner.role.lead.already.existing',
      {
        old_name: oldLeadPartnerName,
        new_name: controls.abbreviation.value
      }
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
    ).subscribe(change => {
      if (change) {
        this.onSubmit(controls, partnerId as any);
      } else {
        this.formService.setDirty(true);
      }
    });
  }

  private redirectToPartnerOverview(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId, 'applicationFormPartner']);
  }

  private redirectToPartnerDetail(partner: any): void {
    this.router.navigate([
      'app', 'project', 'detail', this.projectId, 'applicationFormPartner', 'detail', partner.id
    ]);
  }

}
