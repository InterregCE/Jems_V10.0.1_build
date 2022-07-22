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
import {FormGroup} from '@angular/forms';
import {FormState} from '@common/components/forms/form-state';
import {MatTreeNestedDataSource} from '@angular/material/tree';
import {TranslateService} from '@ngx-translate/core';
import {
  ProgrammeEditableStateStore
} from '../../../programme/programme-page/services/programme-editable-state-store.service';
import {JemsRegionCheckbox} from '@common/models/jems-region-checkbox';
import {Observable, of} from 'rxjs';

@Component({
  selector: 'jems-regions',
  templateUrl: './jems-regions.component.html',
  styleUrls: ['./jems-regions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class JemsRegionsComponent extends ViewEditFormComponent implements OnInit {
  @Input()
  selectedRegions: Map<string, JemsRegionCheckbox[]>;
  @Input()
  dataSource: MatTreeNestedDataSource<JemsRegionCheckbox>;
  @Input()
  isSavedByParent = false;

  @Input()
  isEditable$: Observable<boolean>;

  @Input()
  cancelEditFormParent: Observable<boolean> = of(false);

  @Input()
  formIsEditableByDefault = false;

  @Output()
  selectionChanged = new EventEmitter<void>();
  @Output()
  saveRegions = new EventEmitter<void>();
  @Output()
  cancelEdit = new EventEmitter<void>();

  constructor(
    protected changeDetectorRef: ChangeDetectorRef,
    protected translationService: TranslateService,
    public programmeEditableStateStore: ProgrammeEditableStateStore,
  ) {
    super(changeDetectorRef, translationService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.selectedRegions.size < 1 || this.formIsEditableByDefault) {
      this.changeFormState$.next(FormState.EDIT);
    }
  }

  getForm(): FormGroup | null {
    return null;
  }

  onSubmit(): void {
    this.saveRegions.next();
    this.changeFormState$.next(FormState.VIEW);
  }

  onCancel(): void {
    this.cancelEdit.emit();
    this.changeFormState$.next(FormState.VIEW);
  }

}
