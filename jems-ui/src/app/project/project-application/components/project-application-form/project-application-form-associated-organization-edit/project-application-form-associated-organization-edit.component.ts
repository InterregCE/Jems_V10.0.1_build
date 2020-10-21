import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
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
  InputProjectAssociatedOrganizationCreate,
  InputProjectAssociatedOrganizationUpdate,
  InputProjectContact,
  InputProjectAssociatedOrganizationAddress,
  OutputProjectAssociatedOrganizationDetail,
  OutputProjectPartner,
} from '@cat/api';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {FormState} from '@common/components/forms/form-state';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {Permission} from '../../../../../security/permissions/permission';

@Component({
  selector: 'app-project-application-form-associated-organization-edit',
  templateUrl: './project-application-form-associated-organization-edit.component.html',
  styleUrls: ['./project-application-form-associated-organization-edit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormAssociatedOrganizationEditComponent extends ViewEditForm implements OnInit, OnChanges {
  Permission = Permission;
  Object = Object

  @Input()
  nutsCountry: any;
  @Input()
  nutsRegion2: any;
  @Input()
  nutsRegion3: any[];

  @Input()
  partners: OutputProjectPartner[];
  @Input()
  associatedOrganization: OutputProjectAssociatedOrganizationDetail;
  @Input()
  editable: boolean;

  @Output()
  changeCountry = new EventEmitter<any>();
  @Output()
  changeRegion = new EventEmitter<any>();
  @Output()
  create = new EventEmitter<InputProjectAssociatedOrganizationCreate>();
  @Output()
  update = new EventEmitter<InputProjectAssociatedOrganizationUpdate>();
  @Output()
  cancel = new EventEmitter<void>();

  associatedOrganizationForm: FormGroup = this.formBuilder.group({
    id: [],
    nameInOriginalLanguage: ['', Validators.compose([
      Validators.maxLength(100),
      Validators.required])
    ],
    nameInEnglish: ['', Validators.compose([
      Validators.maxLength(100),
      Validators.required])
    ],
    partnerId: [null, Validators.required],
    country: [''],
    nutsRegion2: [''],
    nutsRegion3: [''],
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
    contactEmail: ['', Validators.compose([
      Validators.maxLength(255),
      Validators.email
    ])],
    contactTelephone: ['', Validators.compose([
      Validators.maxLength(25),
      Validators.pattern('^[0-9+()/-]*$')
    ])],
    roleDescription: ['', Validators.maxLength(2000)],
  })

  nameInOriginalLanguageErrors = {
    maxlength: 'project.organization.original.name.size.too.long',
    required: 'project.organization.original.should.not.be.empty',
  };
  nameInEnglishErrors = {
    maxlength: 'project.organization.english.name.size.too.long',
    required: 'project.organization.english.should.not.be.empty',
  };
  partnerIdErrors = {
    required: 'project.organization.partner.should.not.be.empty',
  };
  streetErrors = {
    maxlength: 'address.street.size.too.long'
  };
  houseNumberErrors = {
    maxlength: 'address.houseNumber.size.too.long'
  };
  postalCodeErrors = {
    maxlength: 'address.postalCode.size.too.long'
  };
  cityErrors = {
    maxlength: 'address.city.size.too.long'
  };
  representativeTitleErrors = {
    maxlength: 'project.contact.title.size.too.long'
  };
  representativeFirstNameErrors = {
    maxlength: 'project.contact.first.name.size.too.long'
  };
  representativeLastNameErrors = {
    maxlength: 'project.contact.last.name.size.too.long'
  };
  contactTitleErrors = {
    maxlength: 'project.contact.title.size.too.long'
  };
  contactFirstNameErrors = {
    maxlength: 'project.contact.first.name.size.too.long'
  };
  contactLastNameErrors = {
    maxlength: 'project.contact.last.name.size.too.long'
  };
  contactEmailErrors = {
    maxlength: 'project.contact.email.size.too.long',
    email: 'project.contact.email.wrong.format'
  };
  contactTelephoneErrors = {
    maxlength: 'project.contact.telephone.size.too.long',
    pattern: 'project.contact.telephone.wrong.format'
  };
  roleDescriptionErrors = {
    maxlength: 'project.organization.roleDescription.size.too.long',
  };

  constructor(private formBuilder: FormBuilder,
              protected changeDetectorRef: ChangeDetectorRef,
              private sideNavService: SideNavService) {
    super(changeDetectorRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.editable && !this.associatedOrganization?.id) {
      this.changeFormState$.next(FormState.EDIT);
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.associatedOrganization || changes.editable) {
      this.changeFormState$.next(this.editable && !this.associatedOrganization.id ? FormState.EDIT : FormState.VIEW);
    }
  }

  getForm(): FormGroup | null {
    return this.associatedOrganizationForm;
  }

  countryChanged(country: any): void {
    this.associatedOrganizationForm.controls.nutsRegion2.reset();
    this.associatedOrganizationForm.controls.nutsRegion3.reset();
    this.changeCountry.emit(country);
  }

  regionChanged(region: any): void {
    this.associatedOrganizationForm.controls.nutsRegion3.reset();
    this.changeRegion.emit(region);
  }

  onSubmit(): void {
    console.log('submit');
    const toSave = {
      partnerId: this.controls?.partnerId.value,
      nameInOriginalLanguage: this.controls?.nameInOriginalLanguage.value,
      nameInEnglish: this.controls?.nameInEnglish.value,
      address: {
        country: this.controls?.country.value,
        nutsRegion2: this.controls?.nutsRegion2.value,
        nutsRegion3: this.controls?.nutsRegion3.value,
        street: this.controls?.street.value,
        houseNumber: this.controls?.houseNumber.value,
        postalCode: this.controls?.postalCode.value,
        city: this.controls?.city.value,
      } as InputProjectAssociatedOrganizationAddress,
      contacts: this.getContacts(),
      roleDescription: this.controls?.roleDescription.value,
    }

    if (!this.controls?.id?.value)
      this.create.emit({...toSave} as InputProjectAssociatedOrganizationCreate);
    else
      this.update.emit({id: this.controls.id.value, ...toSave} as InputProjectAssociatedOrganizationUpdate);
  }

  private getContacts(): InputProjectContact[] {
    const contacts: InputProjectContact[] = [];

    const contactRepresentative = {
      type: InputProjectContact.TypeEnum.LegalRepresentative,
      title: this.controls?.representativeTitle.value,
      firstName: this.controls?.representativeFirstName.value,
      lastName: this.controls?.representativeLastName.value,
    } as InputProjectContact

    if (contactRepresentative.title || contactRepresentative.firstName || contactRepresentative.lastName)
      contacts.push(contactRepresentative);

    const person = {
      type: InputProjectContact.TypeEnum.ContactPerson,
      title: this.controls?.contactTitle.value,
      firstName: this.controls?.contactFirstName.value,
      lastName: this.controls?.contactLastName.value,
      email: this.controls?.contactEmail.value,
      telephone: this.controls?.contactTelephone.value,
    } as InputProjectContact

    if (person.title || person.firstName || person.lastName || person.email || person.telephone)
      contacts.push(person);

    return contacts;
  }

  onCancel(): void {
    if (!this.associatedOrganization?.id) {
      this.cancel.emit();
    }
    this.changeFormState$.next(FormState.VIEW);
  }

  protected enterViewMode(): void {
    this.initFields();
    this.sideNavService.setAlertStatus(false);
  }

  protected enterEditMode(): void {
    this.initFields();
    this.sideNavService.setAlertStatus(true);
  }

  private initFields() {
    this.controls?.id.setValue(this.associatedOrganization?.id);
    this.controls?.nameInOriginalLanguage.setValue(this.associatedOrganization?.nameInOriginalLanguage);
    this.controls?.nameInEnglish.setValue(this.associatedOrganization?.nameInEnglish);
    this.controls?.partnerId.setValue(this.associatedOrganization?.partner?.id);
    this.controls?.country.setValue(this.associatedOrganization?.address?.country);
    this.controls?.nutsRegion2.setValue(this.associatedOrganization?.address?.nutsRegion2);
    this.controls?.nutsRegion3.setValue(this.associatedOrganization?.address?.nutsRegion3);
    this.controls?.street.setValue(this.associatedOrganization?.address?.street);
    this.controls?.houseNumber.setValue(this.associatedOrganization?.address?.houseNumber);
    this.controls?.postalCode.setValue(this.associatedOrganization?.address?.postalCode);
    this.controls?.city.setValue(this.associatedOrganization?.address?.city);
    this.initLegalRepresentative();
    this.initContactPerson();
    this.controls?.roleDescription.setValue(this.associatedOrganization?.roleDescription);
  }

  private initLegalRepresentative() {
    const legalRepresentative = this.associatedOrganization?.contacts?.find(person => person.type === InputProjectContact.TypeEnum.LegalRepresentative)
    this.associatedOrganizationForm.controls.representativeTitle.setValue(legalRepresentative?.title);
    this.associatedOrganizationForm.controls.representativeFirstName.setValue(legalRepresentative?.firstName);
    this.associatedOrganizationForm.controls.representativeLastName.setValue(legalRepresentative?.lastName);
  }

  private initContactPerson() {
    const contactPerson = this.associatedOrganization?.contacts?.find(person => person.type === InputProjectContact.TypeEnum.ContactPerson)
    this.associatedOrganizationForm.controls.contactTitle.setValue(contactPerson?.title);
    this.associatedOrganizationForm.controls.contactFirstName.setValue(contactPerson?.firstName);
    this.associatedOrganizationForm.controls.contactLastName.setValue(contactPerson?.lastName);
    this.associatedOrganizationForm.controls.contactEmail.setValue(contactPerson?.email);
    this.associatedOrganizationForm.controls.contactTelephone.setValue(contactPerson?.telephone);
  }

}
