import {ChangeDetectionStrategy, Component} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {FormBuilder} from '@angular/forms';
import {ProgrammeUnitCostListDTO} from '@cat/api';
import {FormService} from '@common/components/section/form/form.service';
import {SelectionModel} from '@angular/cdk/collections';
import {catchError, map, take, tap} from 'rxjs/operators';
import {CallStore} from '../../services/call-store.service';
import {combineLatest, Observable} from 'rxjs';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-call-unit-costs',
  templateUrl: './call-unit-costs.component.html',
  styleUrls: ['./call-unit-costs.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallUnitCostsComponent {
  callId = this.activatedRoute?.snapshot?.params?.callId;

  unitCostDataSource = new MatTableDataSource<ProgrammeUnitCostListDTO>();
  selection = new SelectionModel<ProgrammeUnitCostListDTO>(true, []);
  initialSelection = new SelectionModel<ProgrammeUnitCostListDTO>(true, []);

  data$: Observable<{
    callUnitCosts: ProgrammeUnitCostListDTO[],
    programmeUnitCosts: ProgrammeUnitCostListDTO[],
    callIsEditable: boolean,
    callIsPublished: boolean
  }>;

  constructor(private callStore: CallStore,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private activatedRoute: ActivatedRoute) {
    this.formService.setCreation(!this.callId);
    this.data$ = combineLatest([
      this.callStore.call$,
      this.callStore.unitCosts$,
      this.callStore.callIsEditable$,
      this.callStore.callIsPublished$
    ])
      .pipe(
        map(([call, programmeUnitCosts, callIsEditable, callIsPublished]) => ({
          callUnitCosts: call.unitCosts,
          programmeUnitCosts,
          callIsEditable,
          callIsPublished,
        })),
        tap(data => this.resetForm(data.programmeUnitCosts, data.callUnitCosts, data.callIsEditable, data.callIsPublished))
      );
  }

  resetForm(programmeUnitCosts: ProgrammeUnitCostListDTO[], callUnitCosts: ProgrammeUnitCostListDTO[],
            callIsEditable: boolean, callIsPublished: boolean): void {
    this.unitCostDataSource = new MatTableDataSource<ProgrammeUnitCostListDTO>(programmeUnitCosts);
    this.selection.clear();
    this.initialSelection.clear();
    this.unitCostDataSource.data.forEach((unitCost: ProgrammeUnitCostListDTO) => {
      if (callUnitCosts.filter(element => element.id === unitCost.id).length > 0) {
        this.selection.select(unitCost);
        this.initialSelection.select(unitCost);
      }
    });
    this.formService.setEditable(callIsEditable && !callIsPublished);
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

  disabled(element: ProgrammeUnitCostListDTO, data: {callIsEditable: boolean, callIsPublished: boolean}): boolean {
    return !data.callIsEditable || (data.callIsPublished && this.initialSelection.isSelected(element));
  }
}
