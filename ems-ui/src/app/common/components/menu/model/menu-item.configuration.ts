export class MenuItemConfiguration {
  // name of the menu item. Will be used as display name.
  name: string;
  // flag that says if the route is internal or externsl.
  isInternal: boolean;
  // the route to be used.
  route: string;
  // lambda function that will be executed on clicking the menu item.
  action: (internal: boolean, route: string) => void;

  public constructor(init?: Partial<MenuItemConfiguration>) {
    Object.assign(this, init);
  }
}
