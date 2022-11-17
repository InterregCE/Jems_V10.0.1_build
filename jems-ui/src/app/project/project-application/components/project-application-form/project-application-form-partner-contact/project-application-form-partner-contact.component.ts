import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProjectContactDTO, ProjectPartnerDetailDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {ProjectPartnerStore} from '../../../containers/project-application-form-page/services/project-partner-store.service';
import {catchError, filter, take, tap} from 'rxjs/operators';
import {APPLICATION_FORM} from '@project/common/application-form-model';
import {Observable} from 'rxjs';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ActivatedRoute} from '@angular/router';
import {RoutingService} from '@common/services/routing.service';
import {FormVisibilityStatusService} from '@project/common/services/form-visibility-status.service';

@UntilDestroy()
@Component({
  selector: 'jems-project-application-form-partner-contact',
  templateUrl: './project-application-form-partner-contact.component.html',
  styleUrls: ['./project-application-form-partner-contact.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormPartnerContactComponent {
  APPLICATION_FORM = APPLICATION_FORM;

  partner$: Observable<ProjectPartnerDetailDTO>;

  partnerContactForm: FormGroup = this.formBuilder.group({
    partnerRepresentativeTitle: ['', Validators.maxLength(25)],
    partnerRepresentativeFirstName: ['', Validators.maxLength(50)],
    partnerRepresentativeLastName: ['', Validators.maxLength(50)],
    partnerContactTitle: ['', Validators.maxLength(25)],
    partnerContactFirstName: ['', Validators.maxLength(50)],
    partnerContactLastName: ['', Validators.maxLength(50)],
    partnerContactEmail: ['', Validators.compose([
      Validators.email,
      Validators.maxLength(255)
    ])],
    partnerContactTelephone: ['', Validators.compose([
      Validators.pattern('^[0-9 +()/-]*$'),
      Validators.maxLength(25)
    ])]
  });

  partnerContactEmailErrors = {
    email: 'project.contact.email.wrong.format'
  };
  partnerContactTelephoneErrors = {
    pattern: 'project.contact.telephone.wrong.format'
  };

  private static isContactDtoEmpty(contactDto: ProjectContactDTO): boolean {
    return !(contactDto.title || contactDto.firstName || contactDto.lastName ||
      contactDto.email || contactDto.telephone);
  }

  private static getValidatedDataToEmit(legalRepresentative: ProjectContactDTO,
                                        contactPerson: ProjectContactDTO): ProjectContactDTO[] {
    const dataToEmit: ProjectContactDTO[] = [];
    if (!ProjectApplicationFormPartnerContactComponent.isContactDtoEmpty(legalRepresentative)) {
      dataToEmit.push(legalRepresentative);
    }
    if (!ProjectApplicationFormPartnerContactComponent.isContactDtoEmpty(contactPerson)) {
      dataToEmit.push(contactPerson);
    }
    return dataToEmit;
  }

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private partnerStore: ProjectPartnerStore,
              private activatedRoute: ActivatedRoute,
              private router: RoutingService,
              private visibilityStatusService: FormVisibilityStatusService
              ) {
    visibilityStatusService.isVisible$((APPLICATION_FORM.SECTION_B.CONTACT)).pipe(
      untilDestroyed(this),
      filter(isVisible => !isVisible),
      tap(() => this.router.navigate(['../identity'], {relativeTo: this.activatedRoute, queryParamsHandling: 'merge'})),
    ).subscribe();
    this.formService.init(this.partnerContactForm, this.partnerStore.isProjectEditable$);
    this.partner$ = this.partnerStore.partner$
      .pipe(
        tap(partner => this.resetForm(partner))
      );
  }

  onSubmit(): void {
    const legalRepresentative = {
      type: ProjectContactDTO.TypeEnum.LegalRepresentative,
      title: this.partnerContactForm.controls.partnerRepresentativeTitle.value,
      firstName: this.partnerContactForm.controls.partnerRepresentativeFirstName.value,
      lastName: this.partnerContactForm.controls.partnerRepresentativeLastName.value,
      email: '',
      telephone: ''
    };
    const contactPerson = {
      type: ProjectContactDTO.TypeEnum.ContactPerson,
      title: this.partnerContactForm.controls.partnerContactTitle.value,
      firstName: this.partnerContactForm.controls.partnerContactFirstName.value,
      lastName: this.partnerContactForm.controls.partnerContactLastName.value,
      email: this.partnerContactForm.controls.partnerContactEmail.value,
      telephone: this.partnerContactForm.controls.partnerContactTelephone.value
    };

    this.partnerStore.updatePartnerContact(
      ProjectApplicationFormPartnerContactComponent.getValidatedDataToEmit(legalRepresentative, contactPerson)
    )
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.partner.contact.save.success')),
        catchError(error => this.formService.setError(error))
      ).subscribe();
  }

  resetForm(partner: ProjectPartnerDetailDTO): void {
    this.initLegalRepresentative(partner);
    this.initContactPerson(partner);
    this.formService.resetEditable();
  }

  private initLegalRepresentative(partner: ProjectPartnerDetailDTO): void {
    const legalRepresentative = partner?.contacts?.find(person => person.type === ProjectContactDTO.TypeEnum.LegalRepresentative);
    this.partnerContactForm.controls.partnerRepresentativeTitle.setValue(legalRepresentative?.title);
    this.partnerContactForm.controls.partnerRepresentativeFirstName.setValue(legalRepresentative?.firstName);
    this.partnerContactForm.controls.partnerRepresentativeLastName.setValue(legalRepresentative?.lastName);
  }

  private initContactPerson(partner: ProjectPartnerDetailDTO): void {
    const contactPerson = partner?.contacts?.find(person => person.type === ProjectContactDTO.TypeEnum.ContactPerson);
    this.partnerContactForm.controls.partnerContactTitle.setValue(contactPerson?.title);
    this.partnerContactForm.controls.partnerContactFirstName.setValue(contactPerson?.firstName);
    this.partnerContactForm.controls.partnerContactLastName.setValue(contactPerson?.lastName);
    this.partnerContactForm.controls.partnerContactEmail.setValue(contactPerson?.email);
    this.partnerContactForm.controls.partnerContactTelephone.setValue(contactPerson?.telephone);
  }

}
