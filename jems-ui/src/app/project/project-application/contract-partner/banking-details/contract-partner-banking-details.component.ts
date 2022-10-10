import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {combineLatest, Observable, of} from 'rxjs';
import {
    ContractingPartnerBankingDetailsDTO,
    OutputNuts,
    ProjectContractingPartnerBankingDetailsService,
    ProjectPartnerSummaryDTO
} from '@cat/api';
import {ActivatedRoute} from '@angular/router';
import {catchError, map, startWith, switchMap, take, tap} from 'rxjs/operators';
import {ContractPartnerStore} from '@project/project-application/contract-partner/contract-partner.store';
import {
    ProjectAssociatedOrganizationStore
} from '@project/project-application/containers/project-application-form-page/services/project-associated-organization-store.service';
import {ProjectPartnerRoleEnum} from '@project/model/ProjectPartnerRoleEnum';

@UntilDestroy()
@Component({
    selector: 'jems-contract-partner-banking-details',
    templateUrl: './contract-partner-banking-details.component.html',
    styleUrls: ['./contract-partner-banking-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [FormService, ProjectAssociatedOrganizationStore],
})
export class ContractPartnerBankingDetailsComponent {

    partnerId: number;
    projectId: number;

    activeBankingDetails: ContractingPartnerBankingDetailsDTO;
    selectedCountry: OutputNuts | undefined;
    filteredCountry: Observable<string[]>;
    selectedRegion2: OutputNuts | undefined;
    filteredRegion2: Observable<string[]>;
    filteredRegion3: Observable<string[]>;
    nuts: OutputNuts[];

    partnerBankingDetailsForm: FormGroup = this.formBuilder.group({
        accountHolder: ['', Validators.maxLength(100)],
        accountNumber: ['', Validators.maxLength(50)],
        accountIBAN: ['', Validators.maxLength(50)],
        accountSwiftBICCode: ['', Validators.maxLength(50)],
        bankName: ['', Validators.maxLength(100)],
        streetName: ['', Validators.maxLength(50)],
        streetNumber: ['', Validators.maxLength(10)],
        postalCode: ['', Validators.maxLength(10)],
        country: [''],
        countryCode: [''],
        nutsTwoRegion: [''],
        nutsTwoRegionCode: [''],
        nutsThreeRegion: [''],
        nutsThreeRegionCode: [''],
    });

    data$: Observable<{
        bankingDetails: ContractingPartnerBankingDetailsDTO;
        nuts: OutputNuts[];
        canEdit: boolean;
        canView: boolean;
        partnerSummary: ProjectPartnerSummaryDTO;
    }>;

    constructor(
        private activatedRoute: ActivatedRoute,
        private contractPartnerStore: ContractPartnerStore,
        private formBuilder: FormBuilder,
        public formService: FormService,
        private bankingDetailsService: ProjectContractingPartnerBankingDetailsService,
        public associatedOrganizationStore: ProjectAssociatedOrganizationStore
    ) {
        this.formService.init(this.partnerBankingDetailsForm);
        const bankingDetails$ = combineLatest([this.contractPartnerStore.partnerId$, this.contractPartnerStore.projectId$])
            .pipe(
                tap(([partnerId, projectId]) => {
                    this.partnerId = Number(partnerId);
                    this.projectId = projectId;
                }),
                switchMap(([partnerId, projectId]) => this.bankingDetailsService.getBankingDetails(Number(partnerId), projectId)),
                untilDestroyed(this)
            );
        this.data$ = combineLatest([
            bankingDetails$,
            this.associatedOrganizationStore.nuts$,
            this.contractPartnerStore.userCanEditContractPartner$,
            this.contractPartnerStore.userCanViewContractPartner$,
            this.contractPartnerStore.partnerSummary$,
        ]).pipe(
            map(([bankingDetails, nuts, canEdit, canView, partnerSummary]) => ({
                bankingDetails,
                nuts,
                canEdit,
                canView,
                partnerSummary
            })),
            tap(data => this.activeBankingDetails = data.bankingDetails),
            tap(data => this.nuts = data.nuts),
            tap(data => this.resetForm(data.bankingDetails, data.canEdit))
        );
    }

    countryChanged(countryTitle: string): void {
        this.selectedCountry = this.findByName(countryTitle, this.nuts);
        this.partnerBankingDetailsForm.controls.countryCode.patchValue(this.selectedCountry?.code);
        this.partnerBankingDetailsForm.controls.nutsTwoRegion.patchValue('');
        this.partnerBankingDetailsForm.controls.nutsTwoRegionCode.patchValue(null);
        this.region2Changed('');
    }

    region2Changed(region2Title: string): void {
        this.selectedRegion2 = this.findByName(region2Title, this.getRegion2Areas());
        this.partnerBankingDetailsForm.controls.nutsTwoRegion.patchValue(this.selectedRegion2?.code);
        this.partnerBankingDetailsForm.controls.nutsThreeRegion.patchValue('');
        this.partnerBankingDetailsForm.controls.nutsThreeRegionCode.patchValue(null);
    }

    region3Changed(region3Title: string): void {
        const selectedRegion3 = this.findByName(region3Title, this.getRegion3Areas());
        this.partnerBankingDetailsForm.controls.nutsThreeRegionCode.patchValue(selectedRegion3?.code);
    }

    countryUnfocused(event: FocusEvent): void {
        if (ContractPartnerBankingDetailsComponent.selectOptionClicked(event)) {
            return;
        }
        const selected = this.findByName(this.partnerBankingDetailsForm.controls.country.value, this.nuts);
        if (!selected) {
            this.partnerBankingDetailsForm.controls.country.patchValue('');
            this.partnerBankingDetailsForm.controls.countryCode.patchValue(null);
            this.partnerBankingDetailsForm.controls.nutsTwoRegion.patchValue('');
            this.partnerBankingDetailsForm.controls.nutsTwoRegionCode.patchValue(null);
            this.partnerBankingDetailsForm.controls.nutsThreeRegion.patchValue('');
            this.partnerBankingDetailsForm.controls.nutsThreeRegionCode.patchValue(null);
        }
    }

    region2Unfocused(event: FocusEvent): void {
        if (ContractPartnerBankingDetailsComponent.selectOptionClicked(event)) {
            return;
        }
        const selected = this.findByName(this.partnerBankingDetailsForm.controls.nutsTwoRegion.value, this.getRegion2Areas());
        if (!selected) {
            this.partnerBankingDetailsForm.controls.nutsThreeRegion.patchValue('');
            this.partnerBankingDetailsForm.controls.nutsThreeRegionCode.patchValue(null);
        }
    }

    region3Unfocused(event: FocusEvent): void {
        if (ContractPartnerBankingDetailsComponent.selectOptionClicked(event)) {
            return;
        }
        const selected = this.findByName(this.partnerBankingDetailsForm.controls.nutsThreeRegion.value, this.selectedRegion2?.areas || []);
        if (!selected) {
            this.partnerBankingDetailsForm.controls.nutsThreeRegion.patchValue('');
            this.partnerBankingDetailsForm.controls.nutsThreeRegionCode.patchValue(null);
        }
    }

    initializeFilters(): void {
        this.selectedCountry = this.findByName(this.partnerBankingDetailsForm.controls.country.value, this.nuts);
        this.selectedRegion2 = this.findByName(this.partnerBankingDetailsForm.controls.nutsTwoRegion.value, this.getRegion2Areas());
        this.filteredCountry = this.partnerBankingDetailsForm.controls.country.valueChanges
            .pipe(
                startWith(''),
                map(value => this.filter(value, this.nuts))
            );

        this.filteredRegion2 = this.partnerBankingDetailsForm.controls.nutsTwoRegion.valueChanges
            .pipe(
                startWith(''),
                map(value => this.filter(value, this.getRegion2Areas()))
            );

        this.filteredRegion3 = this.partnerBankingDetailsForm.controls.nutsThreeRegion.valueChanges
            .pipe(
                startWith(''),
                map(value => this.filter(value, this.selectedRegion2?.areas || []))
            );
    }

    private findByName(value: string, nuts: OutputNuts[]): OutputNuts | undefined {
        return nuts.find(nut => value === ContractPartnerBankingDetailsComponent.formatRegion(nut));
    }

    private static formatRegion(region: OutputNuts): string {
        return `${region.title} (${region.code})`;
    }

    private filter(value: string, nuts: OutputNuts[]): string[] {
        const filterValue = (value || '').toLowerCase();
        return nuts
            .filter(nut => ContractPartnerBankingDetailsComponent.formatRegion(nut).toLowerCase().includes(filterValue))
            .map(nut => ContractPartnerBankingDetailsComponent.formatRegion(nut));
    }

    private static selectOptionClicked(event: FocusEvent): boolean {
        return !!event.relatedTarget && (event.relatedTarget as any).tagName === 'MAT-OPTION';
    }

    private getRegion2Areas(): OutputNuts[] {
        return this.selectedCountry?.areas.flatMap(region => region.areas) || [];
    }

    private getRegion3Areas(): OutputNuts[] {
        return this.selectedRegion2?.areas || [];
    }

    resetForm(bankingDetails: ContractingPartnerBankingDetailsDTO, editable: boolean = false) {
        this.formService.setEditable(editable);
        this.partnerBankingDetailsForm.controls.accountHolder.setValue(bankingDetails?.accountHolder);
        this.partnerBankingDetailsForm.controls.accountNumber.setValue(bankingDetails?.accountNumber);
        this.partnerBankingDetailsForm.controls.accountIBAN.setValue(bankingDetails?.accountIBAN);
        this.partnerBankingDetailsForm.controls.accountSwiftBICCode.setValue(bankingDetails?.accountSwiftBICCode);
        this.partnerBankingDetailsForm.controls.bankName.setValue(bankingDetails?.bankName);
        this.partnerBankingDetailsForm.controls.streetName.setValue(bankingDetails?.streetName);
        this.partnerBankingDetailsForm.controls.streetNumber.setValue(bankingDetails?.streetNumber);
        this.partnerBankingDetailsForm.controls.postalCode.setValue(bankingDetails?.postalCode);
        this.partnerBankingDetailsForm.controls.country.setValue(bankingDetails?.country);
        this.partnerBankingDetailsForm.controls.nutsTwoRegion.setValue(bankingDetails?.nutsTwoRegion);
        this.partnerBankingDetailsForm.controls.nutsThreeRegion.setValue(bankingDetails?.nutsThreeRegion);
    }

    saveForm() {
        this.bankingDetailsService.updateBankingDetails(this.partnerId, this.projectId, this.convertFormToBankingDetailsDTO())
            .pipe(
                tap(data => this.activeBankingDetails = data),
                take(1),
                tap(() => this.formService.setSuccess('project.application.contract.partner.section.banking.details.save.success')),
                catchError(error => this.formService.setError(error)),
            )
            .subscribe();
    }

    private convertFormToBankingDetailsDTO(): ContractingPartnerBankingDetailsDTO {
        return {
            partnerId: this.partnerId,
            accountHolder: this.partnerBankingDetailsForm.value.accountHolder,
            accountNumber: this.partnerBankingDetailsForm.value.accountNumber,
            accountIBAN: this.partnerBankingDetailsForm.value.accountIBAN,
            accountSwiftBICCode: this.partnerBankingDetailsForm.value.accountSwiftBICCode,
            bankName: this.partnerBankingDetailsForm.value.bankName,
            streetName: this.partnerBankingDetailsForm.value.streetName,
            streetNumber: this.partnerBankingDetailsForm.value.streetNumber,
            postalCode: this.partnerBankingDetailsForm.value.postalCode,
            country: this.partnerBankingDetailsForm.value.country,
            nutsTwoRegion: this.partnerBankingDetailsForm.value.nutsTwoRegion,
            nutsThreeRegion: this.partnerBankingDetailsForm.value.nutsThreeRegion
        };
    }

    getPartnerTranslationString(partner: ProjectPartnerSummaryDTO): Observable<string> {
        if (partner.role === ProjectPartnerRoleEnum.LEAD_PARTNER) {
            return of('project.application.contract.partner.section.banking.details.lead.partner.title');
        }
        return of('project.application.contract.partner.section.banking.details.partner.title');
    }
}
