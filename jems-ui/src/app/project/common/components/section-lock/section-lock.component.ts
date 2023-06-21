import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {PermissionService} from '../../../../security/permissions/permission.service';
import {Observable} from 'rxjs';
import {UserRoleCreateDTO} from '@cat/api';
import {map} from 'rxjs/operators';
import PermissionsEnum = UserRoleCreateDTO.PermissionsEnum;

@Component({
  selector: 'jems-section-lock',
  templateUrl: './section-lock.component.html',
  styleUrls: ['./section-lock.component.scss']
})
export class SectionLockComponent implements OnChanges {


  @Input()
  isLocked: boolean;

  @Output()
  lock = new EventEmitter<any>();

  @Output()
  unlock = new EventEmitter<any>();

  disabled$: Observable<boolean>;


  constructor(private permissionService: PermissionService) {
    this.disabled$ =  this.permissionService.hasPermission(PermissionsEnum.ProjectSetToContracted).pipe(
        map( hasPermission => !hasPermission)
    );
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.isSectionLocked) {
      this.isLocked = changes.isSectionLocked.currentValue;
    }
  }

  lockSection() {
    this.lock.emit();
  }

  unlockSection() {
    this.unlock.emit();
  }
}
