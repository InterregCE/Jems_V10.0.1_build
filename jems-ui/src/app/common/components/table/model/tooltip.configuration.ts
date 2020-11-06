
export class TooltipConfiguration {

  tooltipContent: string;

  tooltipTranslationKey?: string;

  public constructor(init?: Partial<TooltipConfiguration>) {
    Object.assign(this, init);
  }
}
