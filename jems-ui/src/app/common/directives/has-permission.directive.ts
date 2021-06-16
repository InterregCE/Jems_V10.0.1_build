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
  private permissionsNeeded: UserRoleDTO.PermissionsEnum[];

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
  set hasPermission(permission: UserRoleDTO.PermissionsEnum | UserRoleDTO.PermissionsEnum[]) {
    this.permissionsNeeded = Array.isArray(permission) ? permission : [permission];
    this.updateView();
  }

  private updateView(): void {
    const userPermissions: string[] = this.currentUser?.role?.permissions || [];
    const intersection: string[] = this.getArraysIntersection(this.permissionsNeeded, userPermissions);
    const permissionIntersectionExists: boolean = !!intersection.length;

    if (permissionIntersectionExists || this.alternativeCondition) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }

  private getArraysIntersection(a1: string[], a2: string[]): string[] {
    // tslint:disable-next-line:typedef
    return  a1.filter(function(n) { return a2.indexOf(n) !== -1; });
  }

}
