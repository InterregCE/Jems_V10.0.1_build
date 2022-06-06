import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ViewEditFormComponent} from '@common/components/forms/view-edit-form.component';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {FormState} from '@common/components/forms/form-state';
import {Forms} from '../../../../common/utils/forms';
import {filter, take, takeUntil, tap} from 'rxjs/operators';
import {CurrencyDTO, ProgrammeUnitCostDTO} from '@cat/api';
import {SelectionModel} from '@angular/cdk/collections';
import {NumberService} from '../../../../common/services/number.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';
import {TranslateService} from '@ngx-translate/core';
import {CurrencyCodesEnum, CurrencyStore} from '@common/services/currency.store';

@UntilDestroy()
@Component({
  selector: 'jems-programme-unit-cost-detail',
  templateUrl: './programme-unit-cost-detail.component.html',
  styleUrls: ['./programme-unit-cost-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeUnitCostDetailComponent extends ViewEditFormComponent implements OnInit {
  private static readonly PROGRAMME_UNIT_COST_INVALID = 'programme.unitCost.costPerUnit.invalid';

  MIN_VALUE = 0.01;
  MAX_VALUE = 999999999.99;

  @Input()
  unitCost: ProgrammeUnitCostDTO;
  @Input()
  currencies: CurrencyDTO[];
  @Input()
  isCreate: boolean;
  @Output()
  createUnitCost: EventEmitter<ProgrammeUnitCostDTO> = new EventEmitter<ProgrammeUnitCostDTO>();
  @Output()
  updateUnitCost: EventEmitter<ProgrammeUnitCostDTO> = new EventEmitter<ProgrammeUnitCostDTO>();
  @Output()
  cancelCreate: EventEmitter<void> = new EventEmitter<void>();

  isProgrammeSetupLocked: boolean;
  programmeHasContractedProjects: boolean;
  availableCurrencies: CurrencyDTO[];

  unitCostForm = this.formBuilder.group({
    isOneCostCategory: [null, Validators.required],
    name: [[]],
    description: [[]],
    type: [[]],
    costPerUnit: [null, Validators.compose([
      Validators.min(this.MIN_VALUE),
      Validators.max(this.MAX_VALUE),
      Validators.required])
    ],
    costPerUnitForeignCurrency:[null, Validators.compose([
      Validators.min(this.MIN_VALUE),
      Validators.max(this.MAX_VALUE)])
    ],
    foreignCurrencyCode: [null,[]],
    categories: ['', Validators.required]
  });

  costErrors = {
    required: ProgrammeUnitCostDetailComponent.PROGRAMME_UNIT_COST_INVALID,
    min: ProgrammeUnitCostDetailComponent.PROGRAMME_UNIT_COST_INVALID,
    max: ProgrammeUnitCostDetailComponent.PROGRAMME_UNIT_COST_INVALID
  };

  costForeignCurrencyErrors = {
    min: ProgrammeUnitCostDetailComponent.PROGRAMME_UNIT_COST_INVALID,
    max: ProgrammeUnitCostDetailComponent.PROGRAMME_UNIT_COST_INVALID
  };

  categoriesErrorsMultiple = {
    required: 'unit.cost.categories.should.not.be.empty.multiple'
  };

  categoriesErrorsSingle = {
    required: 'unit.cost.categories.should.not.be.empty.single'
  };

  allowMultipleCostCategoriesErrors = {
    required: 'unit.cost.categories.allow.multiple.should.not.be.empty'
  };

  selectionMultiple = new SelectionModel<ProgrammeUnitCostDTO.CategoriesEnum>(true, []);

  selectionSingle = new SelectionModel<ProgrammeUnitCostDTO.CategoriesEnum>(false, []);

  multipleCostCategories = [
    ProgrammeUnitCostDTO.CategoriesEnum.StaffCosts,
    ProgrammeUnitCostDTO.CategoriesEnum.OfficeAndAdministrationCosts,
    ProgrammeUnitCostDTO.CategoriesEnum.TravelAndAccommodationCosts,
    ProgrammeUnitCostDTO.CategoriesEnum.ExternalCosts,
    ProgrammeUnitCostDTO.CategoriesEnum.EquipmentCosts,
    ProgrammeUnitCostDTO.CategoriesEnum.InfrastructureCosts
  ];
  singleCostCategories = [
    ProgrammeUnitCostDTO.CategoriesEnum.StaffCosts,
    ProgrammeUnitCostDTO.CategoriesEnum.TravelAndAccommodationCosts,
    ProgrammeUnitCostDTO.CategoriesEnum.ExternalCosts,
    ProgrammeUnitCostDTO.CategoriesEnum.EquipmentCosts,
    ProgrammeUnitCostDTO.CategoriesEnum.InfrastructureCosts
  ];
  validNumberOfSelections = false;

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              public programmeEditableStateStore: ProgrammeEditableStateStore,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService,
              public numberService: NumberService,
              private currencyStore: CurrencyStore) {
    super(changeDetectorRef, translationService);

    this.programmeEditableStateStore.isProgrammeEditableDependingOnCall$.pipe(
      tap(isProgrammeEditingLimited => this.isProgrammeSetupLocked = isProgrammeEditingLimited),
      untilDestroyed(this)
    ).subscribe();

    this.programmeEditableStateStore.hasContractedProjects$.pipe(
      tap(hasContractedProjects => this.programmeHasContractedProjects = hasContractedProjects),
      untilDestroyed(this)
    ).subscribe();

     this.currencyStore.currencies$.pipe(
       tap(currencies => this.prepareCurrencyList(currencies)),
       untilDestroyed(this)
     ).subscribe();
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.isCreate) {
      this.changeFormState$.next(FormState.EDIT);
      this.selectionMultiple.clear();
      this.selectionSingle.clear();
    } else {
      this.resetForm();
      this.changeFormState$.next(FormState.VIEW);
    }
  }

  resetForm(): void {
    this.unitCostForm.controls.name.setValue(this.unitCost.name);
    this.unitCostForm.controls.description.setValue(this.unitCost.description);
    this.unitCostForm.controls.type.setValue(this.unitCost.type);
    this.unitCostForm.controls.costPerUnit.setValue(this.unitCost.costPerUnit);
    this.unitCostForm.controls.costPerUnitForeignCurrency.setValue(this.unitCost.costPerUnitForeignCurrency);
    this.unitCostForm.controls.foreignCurrencyCode.setValue(this.unitCost.foreignCurrencyCode);
    this.unitCostForm.controls.isOneCostCategory.setValue(this.unitCost.oneCostCategory);
    this.selectionMultiple.clear();
    this.selectionSingle.clear();
    if (this.unitCost.oneCostCategory) {
      this.selectionSingle.select(this.unitCost.categories[0]);
      this.validNumberOfSelections = this.selectionMultiple.selected.length === 1;
    } else {
      this.unitCost.categories.forEach(category => {
        this.selectionMultiple.select(category);
        this.validNumberOfSelections = this.selectionMultiple.selected.length >= 2;
      });
    }
  }

  getForm(): FormGroup | null {
    return this.unitCostForm;
  }

  onSubmit(): void {
    Forms.confirmDialog(
      this.dialog,
      this.isCreate ? 'unit.cost.final.dialog.title.save' : 'unit.cost.final.dialog.title.update',
      this.isCreate ? 'unit.cost.final.dialog.message.save' : 'unit.cost.final.dialog.message.update'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() => {
      if (this.isCreate) {
        this.createUnitCost.emit({
          name: this.unitCostForm?.controls?.name?.value,
          description: this.unitCostForm?.controls?.description?.value,
          type: this.unitCostForm?.controls?.type?.value,
          costPerUnit: this.unitCostForm?.controls?.costPerUnit?.value,
          costPerUnitForeignCurrency: this.unitCostForm?.controls?.costPerUnitForeignCurrency.value,
          foreignCurrencyCode: this.unitCostForm?.controls?.foreignCurrencyCode.value,
          oneCostCategory: this.unitCostForm?.controls?.isOneCostCategory?.value,
          categories: this.unitCostForm?.controls?.isOneCostCategory?.value ? this.selectionSingle.selected : this.selectionMultiple.selected
        } as ProgrammeUnitCostDTO);
      } else {
        this.updateUnitCost.emit({
          id: this.unitCost?.id,
          name: this.unitCostForm?.controls?.name?.value,
          description: this.unitCostForm?.controls?.description?.value,
          type: this.unitCostForm?.controls?.type?.value,
          costPerUnit: this.unitCostForm?.controls?.costPerUnit?.value,
          costPerUnitForeignCurrency: this.unitCostForm?.controls?.costPerUnitForeignCurrency.value,
          foreignCurrencyCode: this.unitCostForm?.controls?.foreignCurrencyCode.value,
          oneCostCategory: this.unitCostForm?.controls?.isOneCostCategory?.value,
          categories: this.unitCostForm?.controls?.isOneCostCategory?.value ? this.selectionSingle.selected : this.selectionMultiple.selected
        });
      }
    });
  }

  onCancel(): void {
    if (this.isCreate) {
      this.cancelCreate.emit();
    } else {
      this.resetForm();
      this.changeFormState$.next(FormState.VIEW);
    }
  }

  checkSelectionMultiple(element: ProgrammeUnitCostDTO.CategoriesEnum): void {
    this.selectionMultiple.toggle(element);
    this.validNumberOfSelections = this.selectionMultiple.selected.length >= 2;
    if (this.validNumberOfSelections) {
      this.unitCostForm.controls.categories.setValue(true);
    } else {
      this.unitCostForm.controls.categories.setValue(null);
    }
  }

  checkSelectionSingle(element: ProgrammeUnitCostDTO.CategoriesEnum): void {
    this.selectionSingle.toggle(element);
    this.validNumberOfSelections = this.selectionSingle.selected.length === 1;
    if (this.validNumberOfSelections) {
      this.unitCostForm.controls.categories.setValue(true);
    } else {
      this.unitCostForm.controls.categories.setValue(null);
    }
  }

  changeAllowedCategories(value: boolean): void {
    this.unitCostForm.controls.isOneCostCategory.setValue(value);
    this.selectionMultiple.clear();
    this.selectionSingle.clear();
    this.validNumberOfSelections = false;
    this.unitCostForm.controls.categories.setValue(null);
  }

  protected enterEditMode(): void {
    if (this.unitCost) {
      this.unitCostForm.controls.name.setErrors(null);
      this.unitCostForm.controls.description.setErrors(null);
      this.unitCostForm.controls.type.setErrors(null);
      this.unitCostForm.controls.categories.setErrors(null);
    }
    if ((this.unitCost.oneCostCategory && this.unitCost.categories?.length === 1)
      || (!this.unitCost.oneCostCategory && this.unitCost.categories?.length >= 2)) {
      this.validNumberOfSelections = true;
    }
    if (this.isProgrammeSetupLocked && !this.isCreate) {
      this.unitCostForm.controls.costPerUnit.disable();
    }
    if (this.programmeHasContractedProjects && !this.isCreate){
      this.unitCostForm.controls.costPerUnitForeignCurrency.disable();
      this.unitCostForm.controls.foreignCurrencyCode.disable();
    }
  }

  isForeignCurrencySelected(): boolean {
    return this.getForm()?.get('foreignCurrencyCode')?.value;
  }

  prepareCurrencyList(currencies: CurrencyDTO[]) {
    this.availableCurrencies = currencies.filter((el) => el.code !== CurrencyCodesEnum.EUR);
  }

  onForeignCurrencyChange(selectionChange: any) {
    if (!selectionChange.value) {
      this.getForm()?.get('costPerUnitForeignCurrency')?.setValue(null);
    }
  }
}
