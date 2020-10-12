export class HeadlineRoute {
  headline: {i18nKey: string, i18nArguments?: any};
  route?: string;
  scrollRoute?: string;
  scrollToTop? = false;
  bullets?: HeadlineRoute[] = [];
}
