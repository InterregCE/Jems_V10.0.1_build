import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {FormService} from '@common/components/section/form/form.service';
import {MatTableDataSource} from '@angular/material/table';
import {ProgrammeLumpSumListDTO, InputTranslation, CallDetailDTO} from '@cat/api';
import {SelectionModel} from '@angular/cdk/collections';
import {CallStore} from '../../../services/call-store.service';
import {catchError, take, tap} from 'rxjs/operators';
import {LanguageService} from '../../../../common/services/language.service';

@Component({
  selector: 'app-call-lump-sums',
  templateUrl: './call-lump-sums.component.html',
  styleUrls: ['./call-lump-sums.component.scss'],
  providers: [FormService],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CallLumpSumsComponent implements OnInit {

  @Input()
  lumpSums: ProgrammeLumpSumListDTO[];
  @Input()
  call: CallDetailDTO;

  callLumpSumForm: FormGroup;
  published = false;
  selection = new SelectionModel<ProgrammeLumpSumListDTO>(true, []);

  lumpSumDataSource = new MatTableDataSource();

  constructor(public languageService: LanguageService,
              private formBuilder: FormBuilder,
              private formService: FormService,
              private callStore: CallStore) {
  }

  ngOnInit(): void {
    this.initForm();
    this.formService.init(this.callLumpSumForm);
    this.formService.setCreation(!this.call?.id);
    this.published = this.call?.status === CallDetailDTO.StatusEnum.PUBLISHED;
    this.formService.setEditable(!this.published);
  }

  initForm(): void {
    this.lumpSumDataSource = new MatTableDataSource<ProgrammeLumpSumListDTO>(this.lumpSums);
    this.selection.clear();
    this.lumpSumDataSource.data.forEach((lumpSum: ProgrammeLumpSumListDTO) => {
      if (this.call.lumpSums.filter(element => element.id === lumpSum.id).length > 0) {
        this.selection.select(lumpSum);
      }
    });
    this.formService.init(this.callLumpSumForm);
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

  formChanged(): void {
    this.formService.setDirty(true);
  }

  translated(element: InputTranslation[], currentSystemLanguage: string | null): string {
    if (!currentSystemLanguage || !element) {
      return '';
    }
    const elementInSystemLang = element.find((it: InputTranslation) => it.language === currentSystemLanguage);
    return !!elementInSystemLang ? elementInSystemLang.translation : '';
  }
}
