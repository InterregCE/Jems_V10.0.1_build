export interface MenuItemConfiguration {
  // name of the menu item. Will be used as display name.
  name: string;
  // flag that says if the route is internal or external.
  isInternal: boolean;
  // the route to be used.
  route: string;
  icon?: string;
}
