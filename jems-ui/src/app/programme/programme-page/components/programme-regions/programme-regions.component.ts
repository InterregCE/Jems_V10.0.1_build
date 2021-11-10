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
import {ProgrammeRegionCheckbox} from '../../model/programme-region-checkbox';
import {FormState} from '@common/components/forms/form-state';
import {MatTreeNestedDataSource} from '@angular/material/tree';
import {TranslateService} from '@ngx-translate/core';
import {ProgrammeEditableStateStore} from '../../services/programme-editable-state-store.service';

@Component({
  selector: 'app-programme-regions',
  templateUrl: './programme-regions.component.html',
  styleUrls: ['./programme-regions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeRegionsComponent extends ViewEditFormComponent implements OnInit {
  @Input()
  selectedRegions: Map<string, ProgrammeRegionCheckbox[]>;
  @Input()
  dataSource: MatTreeNestedDataSource<ProgrammeRegionCheckbox>;

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
    if (this.selectedRegions.size < 1) {
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
