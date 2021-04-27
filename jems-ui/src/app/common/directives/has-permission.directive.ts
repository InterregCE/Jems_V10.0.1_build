import {
  Directive,
  ElementRef,
  Input,
  OnInit,
  TemplateRef,
  ViewContainerRef
} from '@angular/core';
import {OutputCurrentUser, UserRoleDTO} from '@cat/api';
import {take} from 'rxjs/internal/operators';
import {tap} from 'rxjs/operators';
import {SecurityService} from '../../security/security.service';

@Directive({
  // tslint:disable-next-line:directive-selector
  selector: '[hasPermission]',
})
export class HasPermissionDirective implements OnInit {
  private alternativeCondition: boolean;
  private currentUser: OutputCurrentUser | null;
  private permissionNeeded: UserRoleDTO.PermissionsEnum;

  @Input()
  set hasPermissionAlternativeCondition(val: boolean) {
    this.alternativeCondition = val;
  }

  constructor(
    private element: ElementRef,
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private securityService: SecurityService,
  ) {
  }

  ngOnInit(): void {
    this.securityService.currentUser.pipe(
      take(1),
      tap(user => this.currentUser = user),
      tap(() => this.updateView()),
    ).subscribe();
  }

  @Input()
  set hasPermission(permission: UserRoleDTO.PermissionsEnum) {
    this.permissionNeeded = permission;
    this.updateView();
  }

  private updateView(): void {
    const hasEnoughPermissions = this.currentUser && (this.currentUser.role.permissions.includes(this.permissionNeeded));
    if (hasEnoughPermissions || this.alternativeCondition) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }

}
