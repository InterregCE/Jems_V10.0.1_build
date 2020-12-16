import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ProgrammeUnitCostListDTO, OutputCall} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {SelectionModel} from '@angular/cdk/collections';
import {CallStore} from '../../../services/call-store.service';
import {catchError, take, tap} from 'rxjs/operators';

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
  call: OutputCall;

  callUnitCostForm: FormGroup;
  published = false;
  selection = new SelectionModel<ProgrammeUnitCostListDTO>(true, []);

  unitCostDataSource = new MatTableDataSource();

  constructor(private formBuilder: FormBuilder,
              private formService: FormService,
              private callStore: CallStore) {
  }

  ngOnInit(): void {
    this.initForm();
    this.formService.init(this.callUnitCostForm);
    this.formService.setCreation(!this.call?.id);
    this.published = this.call?.status === OutputCall.StatusEnum.PUBLISHED;
    this.formService.setEditable(!this.published);
  }

  initForm(): void {
    this.unitCostDataSource = new MatTableDataSource<ProgrammeUnitCostListDTO>(this.unitCosts);
    this.selection.clear();
    this.unitCostDataSource.data.forEach((unitCost: ProgrammeUnitCostListDTO) => {
      if (this.call.unitCosts.filter(element => element.id === unitCost.id).length > 0) {
        this.selection.select(unitCost);
      }
    });
    this.formService.init(this.callUnitCostForm);
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
