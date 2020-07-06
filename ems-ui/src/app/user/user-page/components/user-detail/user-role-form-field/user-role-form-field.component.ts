import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {OutputUserRole} from '@cat/api';
import {FormControl, Validators} from '@angular/forms';

@Component({
  selector: 'app-user-role-form-field',
  templateUrl: './user-role-form-field.component.html',
  styleUrls: ['./user-role-form-field.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserRoleFormFieldComponent implements OnInit {

  @Input()
  roleId: number;
  @Input()
  roleControl: FormControl;
  @Input()
  roles: OutputUserRole[];

  roleErrors = {
    required: 'user.accountRoleId.should.not.be.empty'
  };

  ngOnInit(): void {
    this.roleControl.setValidators(Validators.required);
    const selectedRole = this.roles.find(role => role.id === this.roleId);
    this.roleControl.setValue(selectedRole);
  }
}
