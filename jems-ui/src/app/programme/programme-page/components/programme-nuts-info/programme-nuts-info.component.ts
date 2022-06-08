import {ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, Output} from '@angular/core';
import {AbstractFormComponent} from '@common/components/forms/abstract-form.component';
import {FormGroup} from '@angular/forms';
import {OutputNutsMetadata, UserRoleDTO} from '@cat/api';
import {TranslateService} from '@ngx-translate/core';
import PermissionsEnum = UserRoleDTO.PermissionsEnum;

@Component({
  selector: 'jems-programme-nuts-info',
  templateUrl: './programme-nuts-info.component.html',
  styleUrls: ['./programme-nuts-info.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProgrammeNutsInfoComponent extends AbstractFormComponent {
  PermissionsEnum = PermissionsEnum;

  @Input()
  metadata: OutputNutsMetadata;

  @Output()
  downloadNuts = new EventEmitter<void>();

  constructor(
    protected changeDetectorRef: ChangeDetectorRef,
    protected translationService: TranslateService,
  ) {
    super(changeDetectorRef, translationService);
  }

  onSubmit(): void {
    this.submitted = true;
    this.downloadNuts.emit();
  }

  getForm(): FormGroup | null {
    return null;
  }
}
