import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, Output} from '@angular/core';
import {AbstractForm} from '@common/components/forms/abstract-form';
import {FormGroup} from '@angular/forms';
import {OutputNutsMetadata} from '@cat/api';

@Component({
  selector: 'app-programme-nuts-info',
  templateUrl: './programme-nuts-info.component.html',
  styleUrls: ['./programme-nuts-info.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeNutsInfoComponent extends AbstractForm {
  @Input()
  metadata: OutputNutsMetadata;

  @Output()
  downloadNuts = new EventEmitter<void>();

  constructor(protected changeDetectorRef: ChangeDetectorRef) {
    super(changeDetectorRef);
  }

  onSubmit(): void {
    this.submitted = true;
    this.downloadNuts.emit();
  }

  getForm(): FormGroup | null {
    return null;
  }
}
