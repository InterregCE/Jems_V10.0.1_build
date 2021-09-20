import {Pipe, PipeTransform} from '@angular/core';

@Pipe({name: 'IsMenuActive', pure: true})
export class IsMenuActivePipe implements PipeTransform {
  transform(currentRoute: string | null, routeLink: string, exact: boolean): boolean {
    if (!currentRoute) { return  false; }
    if (exact) {
      return this.relaxCurrentRout(currentRoute) === routeLink;
    } else {
      return currentRoute?.startsWith(routeLink);
    }
  }

  private relaxCurrentRout(currentRoute: string): string {
    return this.removeCreateSuffix(this.removeParams(currentRoute));
  }

  private removeParams(route: string): string {
    const paramsStartIndex = route.lastIndexOf('?');
    return route.substr(0, paramsStartIndex >= 0 ? paramsStartIndex : route.length);
  }

  private removeCreateSuffix(route: string): string {
    // there is no menu item when creating a new object, so it should make the parent route looks activated
    return route.replace('/create', '');
  }
}

