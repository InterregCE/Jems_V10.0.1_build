import {Component, Input, OnInit} from '@angular/core';
import {ConfirmDialogComponent} from '@common/components/modals/confirm-dialog/confirm-dialog.component';
import {take} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {OutputUserRole} from '@cat/api';
import {FormControl, Validators} from '@angular/forms';

@Component({
  selector: 'app-user-role-form-field',
  templateUrl: './user-role-form-field.component.html',
  styleUrls: ['./user-role-form-field.component.scss']
})
export class UserRoleFormFieldComponent implements OnInit {

  @Input()
  userRole: OutputUserRole;

  @Input()
  roleControl: FormControl;

  @Input()
  roles: OutputUserRole[];

  roleErrors = {
    required: 'user.accountRoleId.should.not.be.empty'
  };

  private selectedRole: OutputUserRole | undefined;

  constructor(private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.roleControl.setValidators(Validators.required);
    this.selectedRole = this.roles.find(role => role.id === this.userRole.id);
    this.roleControl.setValue(this.selectedRole);
  }

  roleChanged(): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      maxWidth: '30rem',
      data: {
        title: 'user.detail.changeRole.dialog.title',
        message: 'user.detail.changeRole.dialog.message'
      }
    });

    dialogRef.afterClosed()
      .pipe(
        take(1),
      )
      .subscribe(dialogResult => {
        this.selectedRole = dialogResult ? this.roleControl.value : this.selectedRole
        this.roleControl.setValue(this.selectedRole);
      });
  }
}
