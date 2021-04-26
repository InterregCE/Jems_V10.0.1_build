import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output
} from '@angular/core';
import {ViewEditForm} from '@common/components/forms/view-edit-form';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {filter, take, takeUntil, tap} from 'rxjs/operators';
import {FormState} from '@common/components/forms/form-state';
import {Permission} from '../../../../security/permissions/permission';
import {
  ProgrammeLumpSumDTO
} from '@cat/api';
import {SelectionModel} from '@angular/cdk/collections';
import {NumberService} from '../../../../common/services/number.service';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';
import {TranslateService} from '@ngx-translate/core';
import {Forms} from '../../../../common/utils/forms';

@UntilDestroy()
@Component({
  selector: 'app-programme-lump-sum-detail',
  templateUrl: './programme-lump-sum-detail.component.html',
  styleUrls: ['./programme-lump-sum-detail.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeLumpSumDetailComponent extends ViewEditForm implements OnInit {

  Permission = Permission;
  ProgrammeLumpSumDTO = ProgrammeLumpSumDTO;
  isProgrammeSetupLocked: boolean;
  MIN_VALUE = 0.01;
  MAX_VALUE =  999999999.99;

  @Input()
  lumpSum: ProgrammeLumpSumDTO;
  @Input()
  isCreate: boolean;
  @Output()
  createLumpSum: EventEmitter<ProgrammeLumpSumDTO> = new EventEmitter<ProgrammeLumpSumDTO>();
  @Output()
  updateLumpSum: EventEmitter<ProgrammeLumpSumDTO> = new EventEmitter<ProgrammeLumpSumDTO>();
  @Output()
  cancelCreate: EventEmitter<void> = new EventEmitter<void>();

  lumpSumForm = this.formBuilder.group({
    name: [[]],
    description: [[]],
    cost: [null, Validators.compose([
      Validators.min(this.MIN_VALUE),
      Validators.max(this.MAX_VALUE),
      Validators.required])
    ],
    allowSplitting: ['', Validators.required],
    phase: ['', Validators.required],
    categories: ['', Validators.required]
  });

  nameErrors = {
    maxlength: 'lump.sum.name.size.too.long',
  };

  descriptionErrors = {
    maxlength: 'lump.sum.description.size.too.long',
  };

  costErrors = {
    required: 'lump.sum.out.of.range',
    min: 'lump.sum.out.of.range',
    max: 'lump.sum.out.of.range'
  };

  allowSplittingErrors = {
    required: 'lump.sum.splitting.should.not.be.empty'
  };

  phaseErrors = {
    required: 'lump.sum.phase.should.not.be.empty'
  };

  categoriesErrors = {
    required: 'lump.sum.categories.should.not.be.empty'
  };

  previousSplitting = '';
  previousPhase = '';
  selection = new SelectionModel<ProgrammeLumpSumDTO.CategoriesEnum>(true, []);
  categories = [
    ProgrammeLumpSumDTO.CategoriesEnum.StaffCosts,
    ProgrammeLumpSumDTO.CategoriesEnum.OfficeAndAdministrationCosts,
    ProgrammeLumpSumDTO.CategoriesEnum.TravelAndAccommodationCosts,
    ProgrammeLumpSumDTO.CategoriesEnum.ExternalCosts,
    ProgrammeLumpSumDTO.CategoriesEnum.EquipmentCosts,
    ProgrammeLumpSumDTO.CategoriesEnum.InfrastructureCosts
  ];
  validNumberOfSelections = false;

  constructor(private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private programmeEditableStateStore: ProgrammeEditableStateStore,
              protected changeDetectorRef: ChangeDetectorRef,
              protected translationService: TranslateService,
              public numberService: NumberService) {
    super(changeDetectorRef, translationService);

    this.programmeEditableStateStore.init();
    this.programmeEditableStateStore.isProgrammeEditableDependingOnCall$.pipe(
        tap(isProgrammeEditingLimited => this.isProgrammeSetupLocked = isProgrammeEditingLimited),
        untilDestroyed(this)
    ).subscribe();
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.isCreate) {
      this.changeFormState$.next(FormState.EDIT);
      this.selection.clear();
    } else {
      this.resetForm();
    }
  }

  resetForm(): void {
    this.lumpSumForm.controls.name.setValue(this.lumpSum.name);
    this.lumpSumForm.controls.description.setValue(this.lumpSum.description);
    this.lumpSumForm.controls.cost.setValue(this.lumpSum.cost);
    this.previousSplitting = this.lumpSum.splittingAllowed ? 'Yes' : 'No';
    this.previousPhase = this.lumpSum.phase;
    this.selection.clear();
    this.lumpSum.categories.forEach(category => {
      this.selection.select(category);
    });
    this.validNumberOfSelections = this.selection.selected.length >= 2;
    this.changeFormState$.next(FormState.VIEW);
  }

  getForm(): FormGroup | null {
    return this.lumpSumForm;
  }

  onSubmit(): void {
    Forms.confirmDialog(
      this.dialog,
      this.isCreate ? 'lump.sum.final.dialog.title.save' : 'lump.sum.final.dialog.title.update',
      this.isCreate ? 'lump.sum.final.dialog.message.save' : 'lump.sum.final.dialog.message.update'
    ).pipe(
      take(1),
      takeUntil(this.destroyed$),
      filter(yes => !!yes)
    ).subscribe(() => {
      if (this.isCreate) {
        this.createLumpSum.emit({
          name: this.lumpSumForm?.controls?.name?.value,
          description: this.lumpSumForm?.controls?.description?.value,
          cost: this.lumpSumForm?.controls?.cost?.value,
          splittingAllowed: this.previousSplitting === 'Yes',
          phase: this.getCorrectPhase(this.previousPhase),
          categories: this.selection.selected
        } as ProgrammeLumpSumDTO);
      } else {
        this.updateLumpSum.emit({
          id: this.lumpSum?.id,
          name: this.lumpSumForm?.controls?.name?.value,
          description: this.lumpSumForm?.controls?.description?.value,
          cost: this.lumpSumForm?.controls?.cost?.value,
          splittingAllowed: this.previousSplitting === 'Yes',
          phase: this.getCorrectPhase(this.previousPhase),
          categories: this.selection.selected
        });
      }
    });
  }

  onCancel(): void {
    if (this.isCreate) {
      this.cancelCreate.emit();
    } else {
      this.resetForm();
    }
  }

  changeSplitting(value: string): void {
    this.lumpSumForm.controls.allowSplitting.setValue(value);
    this.previousSplitting = value;
  }

  changePhase(value: string): void {
    this.lumpSumForm.controls.phase.setValue(value);
    this.previousPhase = value;
  }

  getCorrectPhase(value: string): ProgrammeLumpSumDTO.PhaseEnum {
    if (value === ProgrammeLumpSumDTO.PhaseEnum.Preparation) {
      return ProgrammeLumpSumDTO.PhaseEnum.Preparation;
    }

    if (value === ProgrammeLumpSumDTO.PhaseEnum.Implementation) {
      return ProgrammeLumpSumDTO.PhaseEnum.Implementation;
    }
    return ProgrammeLumpSumDTO.PhaseEnum.Closure;
  }

  checkSelection(element: ProgrammeLumpSumDTO.CategoriesEnum): void {
    this.selection.toggle(element);
    this.validNumberOfSelections = this.selection.selected.length >= 2;
    if (this.validNumberOfSelections) {
      this.lumpSumForm.controls.categories.setValue(true);
    } else {
      this.lumpSumForm.controls.categories.setValue(null);
    }
  }

  protected enterEditMode(): void {
    if (this.lumpSum) {
      this.lumpSumForm.controls.name.setErrors(null);
      this.lumpSumForm.controls.description.setErrors(null);
      this.lumpSumForm.controls.allowSplitting.setErrors(null);
      this.lumpSumForm.controls.phase.setErrors(null);
      this.lumpSumForm.controls.categories.setErrors(null);
    }
    if (this.isProgrammeSetupLocked && !this.isCreate) {
      this.lumpSumForm.controls.cost.disable();
    }
  }

}
