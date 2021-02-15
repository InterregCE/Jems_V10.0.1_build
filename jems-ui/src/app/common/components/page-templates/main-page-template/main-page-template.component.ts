import {ChangeDetectionStrategy, Component, ElementRef, Input, ViewChild} from '@angular/core';

@Component({
  selector: 'app-main-page-template',
  templateUrl: './main-page-template.component.html',
  styleUrls: ['./main-page-template.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MainPageTemplateComponent {

  @Input() needsCard = false;

  @Input() titleText: string;
  @Input() titleKey: string;

  @Input() subTitleText: string;
  @Input() subTitleKey: string;

  @Input() descriptionText: string;
  @Input() descriptionKey: string;


  @ViewChild('container')
  container: ElementRef;
  isScrollButtonVisible = false;

  scrollToTop(): void {
    this.container.nativeElement.scrollTop = 0;
  }

  onScroll(): void {
    this.isScrollButtonVisible = this.container.nativeElement.scrollTop > 0;
  }

}
