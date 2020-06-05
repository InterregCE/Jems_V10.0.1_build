export class ActionConfiguration {
  // font awesome icon that will be shown for this action.
  icon: string;
  // lambda function that will be executed on clicking the icon.
  action: (element: any) => {};

  // When defining a new instance, we need to define both the icon and the lambda function to be used.
  constructor(icon: string, action: any) {
    this.icon = icon;
    this.action = action;
  }
}
