import {ChangeDetectionStrategy, Component} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {MatTableDataSource} from '@angular/material/table';
import {ProgrammeLumpSumListDTO} from '@cat/api';
import {SelectionModel} from '@angular/cdk/collections';
import {catchError, map, take, tap} from 'rxjs/operators';
import {UntilDestroy} from '@ngneat/until-destroy';
import {CallStore} from '../../services/call-store.service';
import {ActivatedRoute} from '@angular/router';
import {combineLatest, Observable} from 'rxjs';

@UntilDestroy()
@Component({
  selector: 'app-call-lump-sums',
  templateUrl: './call-lump-sums.component.html',
  styleUrls: ['./call-lump-sums.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallLumpSumsComponent {
  callId = this.activatedRoute?.snapshot?.params?.callId;
  lumpSumDataSource = new MatTableDataSource<ProgrammeLumpSumListDTO>();
  selection = new SelectionModel<ProgrammeLumpSumListDTO>(true, []);
  initialSelection = new SelectionModel<ProgrammeLumpSumListDTO>(true, []);

  data$: Observable<{
    callLumpSums: ProgrammeLumpSumListDTO[];
    programmeLumpSums: ProgrammeLumpSumListDTO[];
    callIsEditable: boolean;
    callIsPublished: boolean;
  }>;

  constructor(private callStore: CallStore,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private activatedRoute: ActivatedRoute) {
    this.formService.setCreation(!this.callId);
    this.data$ = combineLatest([
      this.callStore.call$,
      this.callStore.lumpSums$,
      this.callStore.callIsEditable$,
      this.callStore.callIsPublished$
    ])
      .pipe(
        map(([call, programmeLumpSums, callIsEditable, callIsPublished]) => ({
          callLumpSums: call.lumpSums,
          programmeLumpSums,
          callIsEditable,
          callIsPublished
        })),
        tap(data => this.resetForm(data.programmeLumpSums, data.callLumpSums, data.callIsEditable, data.callIsPublished))
      );
  }

  resetForm(programmeLumpSums: ProgrammeLumpSumListDTO[], callLumpSums: ProgrammeLumpSumListDTO[],
            callIsEditable: boolean, callIsPublished: boolean): void {
    this.lumpSumDataSource = new MatTableDataSource<ProgrammeLumpSumListDTO>(programmeLumpSums);
    this.selection.clear();
    this.initialSelection.clear();
    this.lumpSumDataSource.data.forEach((lumpSum: ProgrammeLumpSumListDTO) => {
      if (callLumpSums.filter(element => element.id === lumpSum.id).length > 0) {
        this.selection.select(lumpSum);
        this.initialSelection.select(lumpSum);
      }
    });
    this.formService.setEditable(callIsEditable && !callIsPublished);
  }

  onSubmit(): void {
    const lumpSumIds = this.selection.selected.map(element => element.id);
    this.callStore.saveLumpSums(lumpSumIds)
      .pipe(
        take(1),
        tap(() => this.formService.setSuccess('call.detail.lump.sum.updated.success')),
        catchError(err => this.formService.setError(err))
      ).subscribe();
  }

  toggleLumpSum(element: ProgrammeLumpSumListDTO): void {
    this.selection.toggle(element);
    this.formChanged();
  }

  disabled(lumpSum: ProgrammeLumpSumListDTO, data: {callIsEditable: boolean; callIsPublished: boolean}): boolean {
    return !data.callIsEditable || (data.callIsPublished && this.initialSelection.isSelected(lumpSum));
  }

  formChanged(): void {
    this.formService.setDirty(true);
  }
}
