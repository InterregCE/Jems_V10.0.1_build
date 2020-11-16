import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {OutputNuts} from '@cat/api';
import {map, startWith} from 'rxjs/operators';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-project-application-form-address',
  templateUrl: './project-application-form-address.component.html',
  styleUrls: ['./project-application-form-address.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectApplicationFormAddressComponent implements OnInit, OnChanges {

  @Input()
  partnerId: number;
  @Input()
  addressForm: FormGroup;
  @Input()
  nuts: OutputNuts[];

  selectedCountry: OutputNuts | undefined;
  filteredCountry: Observable<string[]>;
  selectedRegion2: OutputNuts | undefined;
  filteredRegion2: Observable<string[]>;
  filteredRegion3: Observable<string[]>;

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
  homepageErrors = {
    maxlength: 'address.homepage.size.too.long'
  };

  ngOnInit(): void {
    this.initializeFilters();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.partnerId) {
      this.initializeFilters();
    }
  }

  countryChanged(countryTitle: string): void {
    this.selectedCountry = this.findByName(countryTitle, this.nuts);
    this.addressForm.controls.region2.patchValue('');
    this.region2Changed('');
  }

  region2Changed(region2Title: string): void {
    this.selectedRegion2 = this.findByName(region2Title, this.selectedCountry?.areas || []);
    this.addressForm.controls.region3.patchValue('');
  }

  countryUnfocused(event: FocusEvent): void {
    if (this.selectOptionClicked(event)) {
      return;
    }
    const selected = this.findByName(this.addressForm.controls.country.value, this.nuts);
    if (!selected) {
      this.addressForm.controls.country.patchValue('');
      this.addressForm.controls.region2.patchValue('');
      this.addressForm.controls.region3.patchValue('');
    }
  }

  region2Unfocused(event: FocusEvent): void {
    if (this.selectOptionClicked(event)) {
      return;
    }
    const selected = this.findByName(this.addressForm.controls.region2.value, this.selectedCountry?.areas || []);
    if (!selected) {
      this.addressForm.controls.region2.patchValue('');
      this.addressForm.controls.region3.patchValue('');
    }
  }

  region3Unfocused(event: FocusEvent): void {
    if (this.selectOptionClicked(event)) {
      return;
    }
    const selected = this.findByName(this.addressForm.controls.region3.value, this.selectedRegion2?.areas || []);
    if (!selected) {
      this.addressForm.controls.region3.patchValue('');
    }
  }

  private initializeFilters(): void {
    this.selectedCountry = this.findByName(this.addressForm.controls.country.value, this.nuts);
    this.selectedRegion2 = this.findByName(this.addressForm.controls.region2.value, this.selectedCountry?.areas || []);
    this.filteredCountry = this.addressForm.controls.country.valueChanges
      .pipe(
        startWith(''),
        map(value => this.filter(value, this.nuts))
      );

    this.filteredRegion2 = this.addressForm.controls.region2.valueChanges
      .pipe(
        startWith(''),
        map(value => this.filter(value, this.selectedCountry?.areas || []))
      );

    this.filteredRegion3 = this.addressForm.controls.region3.valueChanges
      .pipe(
        startWith(''),
        map(value => this.filter(value, this.selectedRegion2?.areas || []))
      );
  }

  private findByName(value: string, nuts: OutputNuts[]): OutputNuts | undefined {
    return nuts.find(nut => value === this.formatRegion(nut));
  }

  private filter(value: string, nuts: OutputNuts[]): string[] {
    const filterValue = (value || '').toLowerCase();
    return nuts
      .filter(nut => this.formatRegion(nut).toLowerCase().includes(filterValue))
      .map(nut => this.formatRegion(nut));
  }

  private selectOptionClicked(event: FocusEvent): boolean {
    return !!event.relatedTarget && (event.relatedTarget as any).tagName === 'MAT-OPTION';
  }

  private formatRegion(region: OutputNuts): string {
    return region.code + '|' + region.title;
  }
}
