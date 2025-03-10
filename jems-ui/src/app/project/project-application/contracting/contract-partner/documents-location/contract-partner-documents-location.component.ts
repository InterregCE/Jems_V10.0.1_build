import {UntilDestroy} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {combineLatest, Observable} from 'rxjs';
import {ContractingPartnerDocumentsLocationDTO, OutputNuts,} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {ContractPartnerStore} from '@project/project-application/contracting/contract-partner/contract-partner.store';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {catchError, filter, map, startWith, take, tap} from 'rxjs/operators';
import {NutsStore} from '@common/services/nuts.store';

@UntilDestroy()
@Component({
  selector: 'jems-contract-partner-documents-location',
  templateUrl: './contract-partner-documents-location.component.html',
  styleUrls: ['./contract-partner-documents-location.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FormService],
})
export class ContractPartnerDocumentsLocationComponent {

  public nutsConfig = {COUNTRY_AND_NUTS: 'application.config.project.partner.secondary-address.country.and.nuts'};
  documentsLocationForm: FormGroup;
  documentsLocationId: number | null = null;
  data$: Observable<{
    partnerId: number;
    documentsLocation: ContractingPartnerDocumentsLocationDTO;
    canEdit: boolean;
    canView: boolean;
    nuts: OutputNuts[];
    isPartnerLocked: boolean;
  }>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private contractPartnerStore: ContractPartnerStore,
    private formBuilder: FormBuilder,
    private nutsStore: NutsStore,
    public formService: FormService,
  ) {
    this.data$ = combineLatest([
      this.contractPartnerStore.partnerId$.pipe(filter(Boolean), map(Number)),
      this.contractPartnerStore.documentsLocation$,
      this.contractPartnerStore.userCanEditContractPartner$,
      this.contractPartnerStore.userCanViewContractPartner$,
      this.nutsStore.getNuts(),
      this.contractPartnerStore.isPartnerLocked$,
    ]).pipe(
      map(([partnerId, documentsLocation, canEdit, canView, nuts, isPartnerLocked]) => ({
        partnerId, documentsLocation, canEdit, canView, nuts, isPartnerLocked
      })),
      tap(data => this.documentsLocationId = data.documentsLocation.id),
      tap(data => this.initForm(data.canEdit, data.isPartnerLocked)),
      tap(data => this.resetForm(data.documentsLocation)),
    );
  }

  private initForm(isEditable: boolean, isPartnerLocked: boolean): void {
    this.documentsLocationForm = this.formBuilder.group({
      title: ['', Validators.maxLength(50)],
      firstName: ['', Validators.maxLength(50)],
      lastName: ['', Validators.maxLength(50)],
      emailAddress: ['', Validators.compose([
        Validators.email,
        Validators.maxLength(255)
      ])],
      telephoneNo: ['', Validators.compose([
        Validators.pattern('^([\s]+[0-9+()/]+)|([0-9+()/]+)[ 0-9+()/-]*$'),
        Validators.maxLength(25)
      ])],
      institutionName: ['', Validators.maxLength(100)],
      street: ['', Validators.maxLength(50)],
      locationNumber: ['', Validators.maxLength(20)],
      postalCode: ['', Validators.maxLength(20)],
      city: ['', Validators.maxLength(50)],
      homepage: ['', Validators.maxLength(250)],
      nuts: this.formBuilder.group({
        country: [''],
        countryCode: [''],
        region2: [''],
        region2Code: [''],
        region3: [''],
        region3Code: ['']
      })
    });
    this.formService.init(this.documentsLocationForm, new Observable<boolean>().pipe(startWith(isEditable && !isPartnerLocked)));
  }

  resetForm(dto: ContractingPartnerDocumentsLocationDTO): void {
    this.documentsLocationForm.controls.title.setValue(dto.title);
    this.documentsLocationForm.controls.firstName.setValue(dto.firstName);
    this.documentsLocationForm.controls.lastName.setValue(dto.lastName);
    this.documentsLocationForm.controls.emailAddress.setValue(dto.emailAddress);
    this.documentsLocationForm.controls.telephoneNo.setValue(dto.telephoneNo);
    this.documentsLocationForm.controls.institutionName.setValue(dto.institutionName);
    this.documentsLocationForm.controls.street.setValue(dto.street);
    this.documentsLocationForm.controls.locationNumber.setValue(dto.locationNumber);
    this.documentsLocationForm.controls.postalCode.setValue(dto.postalCode);
    this.documentsLocationForm.controls.city.setValue(dto.city);
    this.documentsLocationForm.controls.homepage.setValue(dto.homepage);
    this.nuts.country.setValue(dto.country);
    this.nuts.countryCode.setValue(dto.countryCode);
    this.nuts.region2.setValue(dto.nutsTwoRegion);
    this.nuts.region2Code.setValue(dto.nutsTwoRegionCode);
    this.nuts.region3.setValue(dto.nutsThreeRegion);
    this.nuts.region3Code.setValue(dto.nutsThreeRegionCode);
  }

  get nuts(): { [key: string]: AbstractControl } {
    return (this.documentsLocationForm.controls?.nuts as FormGroup).controls;
  }

  saveForm(partnerId: number): void {
    this.contractPartnerStore.updateDocumentsLocation(this.getUpdatedDocumentsLocationDTO(partnerId))
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.contract.partner.section.beneficial.owner.save.success')),
        catchError(err => this.formService.setError(err)),
      ).subscribe();
  }

  private getUpdatedDocumentsLocationDTO(partnerId: number): ContractingPartnerDocumentsLocationDTO {
    return {
      id: this.documentsLocationId,
      partnerId,
      title: this.documentsLocationForm.controls.title.value,
      firstName: this.documentsLocationForm.controls.firstName.value,
      lastName: this.documentsLocationForm.controls.lastName.value,
      emailAddress: this.documentsLocationForm.controls.emailAddress.value,
      telephoneNo: this.documentsLocationForm.controls.telephoneNo.value,
      institutionName: this.documentsLocationForm.controls.institutionName.value,
      street: this.documentsLocationForm.controls.street.value,
      locationNumber: this.documentsLocationForm.controls.locationNumber.value,
      postalCode: this.documentsLocationForm.controls.postalCode.value,
      city: this.documentsLocationForm.controls.city.value,
      homepage: this.documentsLocationForm.controls.homepage.value,
      country: this.nuts.country.value,
      countryCode: this.nuts.countryCode.value,
      nutsTwoRegion: this.nuts.region2.value,
      nutsTwoRegionCode: this.nuts.region2Code.value,
      nutsThreeRegion: this.nuts.region3.value,
      nutsThreeRegionCode: this.nuts.region3Code.value,
    } as ContractingPartnerDocumentsLocationDTO;
  }

}
