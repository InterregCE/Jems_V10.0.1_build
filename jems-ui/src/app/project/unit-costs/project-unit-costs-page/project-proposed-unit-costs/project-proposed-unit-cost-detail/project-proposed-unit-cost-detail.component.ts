import {
  ChangeDetectionStrategy,
  Component,
  OnInit
} from '@angular/core';
import {FormService} from '@common/components/section/form/form.service';
import {CurrencyDTO, ProgrammeUnitCostDTO, ProjectStatusDTO} from '@cat/api';
import {FormBuilder, Validators} from '@angular/forms';
import {SelectionModel} from '@angular/cdk/collections';
import {MatDialog} from '@angular/material/dialog';
import {CurrencyCodesEnum, CurrencyStore} from '@common/services/currency.store';
import {catchError, filter, map, take, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {Forms} from '@common/utils/forms';
import {
  ProjectStore
} from '@project/project-application/containers/project-application-detail/services/project-store.service';
import {ActivatedRoute} from '@angular/router';
import {Log} from '@common/utils/log';
import {ProjectUnitCostsStore} from '@project/unit-costs/project-unit-costs-page/project-unit-costs-store.service';
import {
  ProjectApplicationFormSidenavService
} from '@project/project-application/containers/project-application-form-page/services/project-application-form-sidenav.service';
import { RoutingService } from '@common/services/routing.service';
import { Alert } from '@common/components/forms/alert';
import {BehaviorSubject, combineLatest} from 'rxjs';

@UntilDestroy()
@Component({
  selector: 'jems-project-proposed-unit-cost-detail',
  templateUrl: './project-proposed-unit-cost-detail.component.html',
  styleUrls: ['./project-proposed-unit-cost-detail.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectProposedUnitCostDetailComponent implements OnInit {

  private static readonly PROJECT_UNIT_COST_INVALID = 'programme.unitCost.costPerUnit.invalid';
  Alert = Alert;
  ProjectStatusDTO = ProjectStatusDTO;

  unitCostId = this.activatedRoute?.snapshot?.params?.unitCostId;
  projectId = this.activatedRoute?.snapshot?.params?.projectId;
  isCreate = !this.unitCostId;
  projectStatus: ProjectStatusDTO.StatusEnum;
  private isUnitCostEditable = new BehaviorSubject(false);

  MIN_VALUE = 0.01;
  MAX_VALUE = 999999999.99;

  unitCost: ProgrammeUnitCostDTO;
  availableCurrencies: CurrencyDTO[];

  projectUnitCostForm = this.formBuilder.group({
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
    categories: ['', Validators.required],
    justification: [[]]
  });

  costErrors = {
    required: ProjectProposedUnitCostDetailComponent.PROJECT_UNIT_COST_INVALID,
    min: ProjectProposedUnitCostDetailComponent.PROJECT_UNIT_COST_INVALID,
    max: ProjectProposedUnitCostDetailComponent.PROJECT_UNIT_COST_INVALID
  };

  costForeignCurrencyErrors = {
    min: ProjectProposedUnitCostDetailComponent.PROJECT_UNIT_COST_INVALID,
    max: ProjectProposedUnitCostDetailComponent.PROJECT_UNIT_COST_INVALID
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
              private projectApplicationFormSidenavService: ProjectApplicationFormSidenavService,
              private dialog: MatDialog,
              private currencyStore: CurrencyStore,
              public projectStore: ProjectStore,
              public formService: FormService,
              private activatedRoute: ActivatedRoute,
              private projectUnitCostsStore: ProjectUnitCostsStore,
              private routingService: RoutingService){

    this.projectStore.projectStatus$.pipe(
      tap(status => this.unitCostEditable(status.status)),
      untilDestroyed(this)
    ).subscribe();

    if (this.unitCostId) {
      this.projectUnitCostsStore.unitCost$.pipe(
        tap(unitCostData => Log.info('Fetched output Project Proposed Unit Cost data:', this, unitCostData)),
        tap((data: ProgrammeUnitCostDTO) => this.unitCost = data),
        tap(() => this.resetForm())
      ).subscribe();
    } else {
      this.unitCost = {} as ProgrammeUnitCostDTO;
    }

    this.currencyStore.currencies$.pipe(
      tap(currencies => this.prepareCurrencyList(currencies)),
      untilDestroyed(this)
    ).subscribe();
  }

  ngOnInit(): void {
    this.formService.init(this.projectUnitCostForm, combineLatest([this.projectStore.projectEditable$, this.isUnitCostEditable]).pipe(map(([isProjectEditable, isUnitCostEditable]) => isProjectEditable && isUnitCostEditable)));
    if (this.isCreate) {
      this.selectionMultiple.clear();
      this.selectionSingle.clear();
    }
  }

  resetForm(): void {
    this.projectUnitCostForm.controls.name.setValue(this.unitCost.name);
    this.projectUnitCostForm.controls.description.setValue(this.unitCost.description);
    this.projectUnitCostForm.controls.type.setValue(this.unitCost.type);
    this.projectUnitCostForm.controls.costPerUnit.setValue(this.unitCost.costPerUnit);
    this.projectUnitCostForm.controls.costPerUnitForeignCurrency.setValue(this.unitCost.costPerUnitForeignCurrency);
    this.projectUnitCostForm.controls.foreignCurrencyCode.setValue(this.unitCost.foreignCurrencyCode);
    this.projectUnitCostForm.controls.isOneCostCategory.setValue(this.unitCost.oneCostCategory);
    this.projectUnitCostForm.controls.justification.setValue(this.unitCost.justification);
    this.selectionMultiple.clear();
    this.selectionSingle.clear();
    if (this.unitCost.oneCostCategory) {
      this.selectionSingle.select(this.unitCost.categories[0]);
      this.validNumberOfSelections = this.selectionSingle.selected.length === 1;
    } else {
      this.unitCost.categories.forEach(category => {
        this.selectionMultiple.select(category);
        this.validNumberOfSelections = this.selectionMultiple.selected.length >= 2;
      });
    }
    if (this.validNumberOfSelections) {
      this.projectUnitCostForm.controls.categories.setValue(true);
    } else {
      this.projectUnitCostForm.controls.categories.setValue(null);
    }
  }

  onSubmit(): void {
    if (this.isCreate) {
      this.createUnitCost({
        name: this.projectUnitCostForm?.controls?.name?.value,
        description: this.projectUnitCostForm?.controls?.description?.value,
        type: this.projectUnitCostForm?.controls?.type?.value,
        costPerUnit: this.projectUnitCostForm?.controls?.costPerUnit?.value,
        costPerUnitForeignCurrency: this.projectUnitCostForm?.controls?.costPerUnitForeignCurrency.value,
        foreignCurrencyCode: this.projectUnitCostForm?.controls?.foreignCurrencyCode.value,
        oneCostCategory: this.projectUnitCostForm?.controls?.isOneCostCategory?.value,
        categories: this.projectUnitCostForm?.controls?.isOneCostCategory?.value ? this.selectionSingle.selected : this.selectionMultiple.selected,
        justification: this.projectUnitCostForm?.controls?.justification.value
      } as ProgrammeUnitCostDTO);
    } else {
      const updateValue = {
        id: this.unitCost?.id,
        name: this.projectUnitCostForm?.controls?.name?.value,
        description: this.projectUnitCostForm?.controls?.description?.value,
        type: this.projectUnitCostForm?.controls?.type?.value,
        costPerUnit: this.projectUnitCostForm?.controls?.costPerUnit?.value,
        costPerUnitForeignCurrency: this.projectUnitCostForm?.controls?.costPerUnitForeignCurrency.value,
        foreignCurrencyCode: this.projectUnitCostForm?.controls?.foreignCurrencyCode.value,
        oneCostCategory: this.projectUnitCostForm?.controls?.isOneCostCategory?.value,
        categories: this.projectUnitCostForm?.controls?.isOneCostCategory?.value ? this.selectionSingle.selected : this.selectionMultiple.selected,
        justification: this.projectUnitCostForm?.controls?.justification.value
      } as ProgrammeUnitCostDTO;

      if (this.isCategorySelectionDifferent()) {
        Forms.confirm(
          this.dialog,
          {
            title: 'unit.cost.final.dialog.title.update',
            message: {
              i18nKey: 'project.application.form.section.part.e.subsection.two.subsection.one.unit.cost.update.message'
            },
          }).pipe(
          take(1),
          tap((answer) => {
            if (answer) {
              this.updateUnitCost(updateValue);
            } else {
              this.resetForm();
            }
          }))
          .subscribe();
      } else {
        this.updateUnitCost(updateValue);
      }
    }
  }

  onCancel(): void {
    if (this.isCreate) {
      this.cancelCreate();
    } else {
      this.resetForm();
    }
  }

  checkSelectionMultiple(element: ProgrammeUnitCostDTO.CategoriesEnum): void {
    this.selectionMultiple.toggle(element);
    this.validNumberOfSelections = this.selectionMultiple.selected.length >= 2;
    if (this.validNumberOfSelections) {
      this.projectUnitCostForm.controls.categories.setValue(true);
    } else {
      this.projectUnitCostForm.controls.categories.setValue(null);
    }
    this.formService.setDirty(true);
  }

  checkSelectionSingle(element: ProgrammeUnitCostDTO.CategoriesEnum): void {
    this.selectionSingle.toggle(element);
    this.validNumberOfSelections = this.selectionSingle.selected.length === 1;
    if (this.validNumberOfSelections) {
      this.projectUnitCostForm.controls.categories.setValue(true);
    } else {
      this.projectUnitCostForm.controls.categories.setValue(null);
    }
    this.formService.setDirty(true);
  }

  changeAllowedCategories(value: boolean): void {
    this.projectUnitCostForm.controls.isOneCostCategory.setValue(value);
    this.selectionMultiple.clear();
    this.selectionSingle.clear();
    this.validNumberOfSelections = false;
    this.projectUnitCostForm.controls.categories.setValue(null);
    this.formService.setDirty(true);
  }

  isForeignCurrencySelected(): boolean {
    return this.projectUnitCostForm?.get('foreignCurrencyCode')?.value;
  }

  prepareCurrencyList(currencies: CurrencyDTO[]) {
    this.availableCurrencies = currencies.filter((el) => el.code !== CurrencyCodesEnum.EUR);
  }

  onForeignCurrencyChange(selectionChange: any) {
    if (!selectionChange.value) {
      this.projectUnitCostForm?.get('costPerUnitForeignCurrency')?.setValue(null);
    }
    this.formService.setDirty(true);
  }

  createUnitCost(unitCost: ProgrammeUnitCostDTO): void {
    this.projectUnitCostsStore.saveUnitCost(unitCost, this.projectId)
      .pipe(
        take(1),
        catchError(error => this.formService.setError(error)),
        tap((created) => this.routingService.navigate(['..', created.id], {relativeTo: this.activatedRoute})),
      ).subscribe();
  }

  updateUnitCost(unitCost: ProgrammeUnitCostDTO): void {
    this.projectUnitCostsStore.updateUnitCost(unitCost)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('project.application.form.section.part.e.unit.cost.save.success')),
        catchError(error => this.formService.setError(error)),
        untilDestroyed(this)
      ).subscribe();
  }

  cancelCreate(): void {
    this.routingService.navigate(['..'], {relativeTo: this.activatedRoute});
  }

  private isCategorySelectionDifferent(): boolean {
    if (this.unitCost.oneCostCategory === this.projectUnitCostForm?.controls?.isOneCostCategory?.value && this.unitCost.oneCostCategory) {
      return !this.areCategoriesEqual(this.unitCost.categories, this.selectionSingle.selected);
    }

    // if (this.unitCost.oneCostCategory === this.projectUnitCostForm?.controls?.isOneCostCategory?.value && !this.unitCost.oneCostCategory) {
    //   return !this.areCategoriesEqual(this.unitCost.categories, this.selectionMultiple.selected);
    // }

    return this.unitCost.oneCostCategory !== this.projectUnitCostForm?.controls?.isOneCostCategory?.value;
  }

  private areCategoriesEqual(original: ProgrammeUnitCostDTO.CategoriesEnum[], modified: ProgrammeUnitCostDTO.CategoriesEnum[]): boolean {
    if (original === modified)
      {return true;}
    if (original == null || modified == null)
      {return false;}
    if (original.length !== modified.length)
      {return false;}

    for (let i = 0; i < original.length; ++i) {
      if (original[i] !== modified[i])
        {return false;}
    }
    return true;
  }

  private unitCostEditable(status: ProjectStatusDTO.StatusEnum): void {
    this.projectStatus = status;
    if (!this.isCreate && status === ProjectStatusDTO.StatusEnum.INMODIFICATION) {
      this.isUnitCostEditable.next(false);
    }
    else {
      this.isUnitCostEditable.next(true);
    }
  }
}
