import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ProgrammeUnitCostListDTO, CallDetailDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {SelectionModel} from '@angular/cdk/collections';
import {CallStore} from '../../../services/call-store.service';
import {catchError, take, tap} from 'rxjs/operators';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-call-unit-costs',
  templateUrl: './call-unit-costs.component.html',
  styleUrls: ['./call-unit-costs.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallUnitCostsComponent implements OnInit {

  @Input()
  unitCosts: ProgrammeUnitCostListDTO[];
  @Input()
  call: CallDetailDTO;
  @Input()
  isApplicant: boolean;

  callUnitCostForm: FormGroup;
  published = false;
  initialSelection = new SelectionModel<ProgrammeUnitCostListDTO>(true, []);
  selection = new SelectionModel<ProgrammeUnitCostListDTO>(true, []);

  unitCostDataSource = new MatTableDataSource();

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              public callStore: CallStore) {
  }

  ngOnInit(): void {
    this.initForm();
    this.formService.init(this.callUnitCostForm);
    this.formService.setCreation(!this.call?.id);
    this.published = this.call?.status === CallDetailDTO.StatusEnum.PUBLISHED;
    this.formService.setEditable(!this.published);
  }

  initForm(): void {
    this.unitCostDataSource = new MatTableDataSource<ProgrammeUnitCostListDTO>(this.unitCosts);
    this.initialSelection.clear();
    this.selection.clear();
    this.unitCostDataSource.data.forEach((unitCost: ProgrammeUnitCostListDTO) => {
      if (this.call.unitCosts.filter(element => element.id === unitCost.id).length > 0) {
        this.selection.select(unitCost);
        this.initialSelection.select(unitCost);
      }
    });
    this.formService.init(this.callUnitCostForm);
    this.callStore.call$.pipe(
        untilDestroyed(this)
    ).subscribe((call: CallDetailDTO) => {
      this.initialSelection.clear();
      this.unitCostDataSource.data.forEach((unitCost: ProgrammeUnitCostListDTO) => {
        if (call.unitCosts.filter(element => element.id === unitCost.id).length > 0) {
          this.initialSelection.select(unitCost);
        }
      });
    });
  }

  onSubmit(): void {
    const unitCostIds = this.selection.selected.map(element => element.id);
    this.callStore.saveUnitCosts(unitCostIds)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('call.detail.unit.cost.updated.success')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  toggleUnitCost(element: ProgrammeUnitCostListDTO): void {
    this.selection.toggle(element);
    this.formChanged();
  }

  formChanged(): void {
    this.formService.setDirty(true);
  }
}
