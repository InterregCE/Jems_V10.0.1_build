import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {combineLatest} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {ActivatedRoute} from '@angular/router';
import {
  InputProjectAssociatedOrganizationAddress,
  ProjectContactDTO,
  InputTranslation,
  OutputProjectAssociatedOrganizationDetail
} from '@cat/api';
import {ProjectAssociatedOrganizationStore} from '../../services/project-associated-organization-store.service';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {take} from 'rxjs/internal/operators';
import {RoutingService} from '@common/services/routing.service';
import {Permission} from '../../../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-form-associated-org-detail',
  templateUrl: './project-application-form-associated-org-detail.component.html',
  styleUrls: ['./project-application-form-associated-org-detail.component.scss'],
  providers: [FormService, ProjectAssociatedOrganizationStore],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormAssociatedOrgDetailComponent implements OnInit {
  Permission = Permission;
  LANGUAGE = InputTranslation.LanguageEnum;

  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  associatedOrganizationId = this.activatedRoute?.snapshot?.params?.associatedOrganizationId;

  data$ = combineLatest([
    this.associatedOrganizationStore.associatedOrganization$,
    this.associatedOrganizationStore.nuts$,
    this.associatedOrganizationStore.dropdownPartners$,
    this.associatedOrganizationStore.projectTitle$
  ])
    .pipe(
      map(([organization, nuts, partners, projectTitle]) => ({organization, nuts, partners, projectTitle})),
      tap(details => this.resetForm(details.organization as OutputProjectAssociatedOrganizationDetail))
    );

  associatedOrganizationForm: FormGroup = this.formBuilder.group({
    id: [],
    nameInOriginalLanguage: ['', [Validators.maxLength(100), Validators.required]],
    nameInEnglish: [[], [Validators.maxLength(100), Validators.required]],
    partnerId: [null, Validators.required],
    country: [''],
    region2: [''],
    region3: [''],
    street: ['', Validators.maxLength(50)],
    houseNumber: ['', Validators.maxLength(20)],
    postalCode: ['', Validators.maxLength(20)],
    city: ['', Validators.maxLength(50)],
    representativeTitle: ['', Validators.maxLength(25)],
    representativeFirstName: ['', Validators.maxLength(50)],
    representativeLastName: ['', Validators.maxLength(50)],
    contactTitle: ['', Validators.maxLength(25)],
    contactFirstName: ['', Validators.maxLength(50)],
    contactLastName: ['', Validators.maxLength(50)],
    contactEmail: ['', [Validators.maxLength(255), Validators.email]],
    contactTelephone: ['', [Validators.maxLength(25), Validators.pattern('^[0-9+()/-]*$')]],
    roleDescription: [],
  });

  contactEmailErrors = {
    email: 'project.contact.email.wrong.format'
  };
  contactTelephoneErrors = {
    pattern: 'project.contact.telephone.wrong.format'
  };

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              public associatedOrganizationStore: ProjectAssociatedOrganizationStore,
              private activatedRoute: ActivatedRoute,
              private router: RoutingService) {
  }

  ngOnInit(): void {
    this.formService.init(this.associatedOrganizationForm, this.associatedOrganizationStore.organizationEditable$);
    this.formService.setCreation(!this.associatedOrganizationId);
  }

  get controls(): { [key: string]: AbstractControl } | undefined {
    return this.associatedOrganizationForm.controls;
  }

  onSubmit(): void {
    const toSave = {
      partnerId: this.controls?.partnerId.value,
      nameInOriginalLanguage: this.controls?.nameInOriginalLanguage.value,
      nameInEnglish: this.controls?.nameInEnglish.value[0].translation,
      address: {
        country: this.controls?.country.value,
        nutsRegion2: this.controls?.region2.value,
        nutsRegion3: this.controls?.region3.value,
        street: this.controls?.street.value,
        houseNumber: this.controls?.houseNumber.value,
        postalCode: this.controls?.postalCode.value,
        city: this.controls?.city.value,
      } as InputProjectAssociatedOrganizationAddress,
      contacts: this.getContacts(),
      roleDescription: this.controls?.roleDescription.value,
    };

    if (!this.controls?.id?.value) {
      this.associatedOrganizationStore.createAssociatedOrganization({id: 0, ...toSave})
        .pipe(
          take(1)
        ).subscribe();
    } else {
      this.associatedOrganizationStore.updateAssociatedOrganization({id: this.controls.id.value, ...toSave})
        .pipe(
          take(1),
          tap(() => this.formService.setSuccess('project.partner.save.success'))
        ).subscribe();
    }
  }

  private getContacts(): ProjectContactDTO[] {
    const contacts: ProjectContactDTO[] = [];

    const contactRepresentative = {
      type: ProjectContactDTO.TypeEnum.LegalRepresentative,
      title: this.controls?.representativeTitle.value,
      firstName: this.controls?.representativeFirstName.value,
      lastName: this.controls?.representativeLastName.value,
    } as ProjectContactDTO;

    if (contactRepresentative.title || contactRepresentative.firstName || contactRepresentative.lastName) {
      contacts.push(contactRepresentative);
    }

    const person = {
      type: ProjectContactDTO.TypeEnum.ContactPerson,
      title: this.controls?.contactTitle.value,
      firstName: this.controls?.contactFirstName.value,
      lastName: this.controls?.contactLastName.value,
      email: this.controls?.contactEmail.value,
      telephone: this.controls?.contactTelephone.value,
    } as ProjectContactDTO;

    if (person.title || person.firstName || person.lastName || person.email || person.telephone) {
      contacts.push(person);
    }

    return contacts;
  }

  onCancel(organization: OutputProjectAssociatedOrganizationDetail): void {
    if (!this.associatedOrganizationId) {
      this.redirectToAssociatedOrganizationOverview();
    }
    this.resetForm(organization);
  }

  resetForm(organization: OutputProjectAssociatedOrganizationDetail): void {
    this.controls?.id.setValue(organization?.id);
    this.controls?.nameInOriginalLanguage.setValue(organization?.nameInOriginalLanguage);
    this.controls?.nameInEnglish.setValue([{
      language: this.LANGUAGE.EN,
      translation: organization?.nameInEnglish || ''
    }]);
    this.controls?.partnerId.setValue(organization?.partner?.id);
    this.controls?.country.setValue(organization?.address?.country);
    this.controls?.region2.setValue(organization?.address?.nutsRegion2);
    this.controls?.region3.setValue(organization?.address?.nutsRegion3);
    this.controls?.street.setValue(organization?.address?.street);
    this.controls?.houseNumber.setValue(organization?.address?.houseNumber);
    this.controls?.postalCode.setValue(organization?.address?.postalCode);
    this.controls?.city.setValue(organization?.address?.city);
    this.initLegalRepresentative(organization);
    this.initContactPerson(organization);
    this.controls?.roleDescription.setValue(organization?.roleDescription);
  }

  private initLegalRepresentative(organization: OutputProjectAssociatedOrganizationDetail): void {
    const legalRepresentative = organization?.contacts?.find(person => person.type === ProjectContactDTO.TypeEnum.LegalRepresentative);
    this.associatedOrganizationForm.controls.representativeTitle.setValue(legalRepresentative?.title);
    this.associatedOrganizationForm.controls.representativeFirstName.setValue(legalRepresentative?.firstName);
    this.associatedOrganizationForm.controls.representativeLastName.setValue(legalRepresentative?.lastName);
  }

  private initContactPerson(organization: OutputProjectAssociatedOrganizationDetail): void {
    const contactPerson = organization?.contacts?.find(person => person.type === ProjectContactDTO.TypeEnum.ContactPerson);
    this.associatedOrganizationForm.controls.contactTitle.setValue(contactPerson?.title);
    this.associatedOrganizationForm.controls.contactFirstName.setValue(contactPerson?.firstName);
    this.associatedOrganizationForm.controls.contactLastName.setValue(contactPerson?.lastName);
    this.associatedOrganizationForm.controls.contactEmail.setValue(contactPerson?.email);
    this.associatedOrganizationForm.controls.contactTelephone.setValue(contactPerson?.telephone);
  }

  redirectToAssociatedOrganizationOverview(): void {
    this.router.navigate(['app', 'project', 'detail', this.projectId, 'applicationFormAssociatedOrganization']);
  }
}
