import {ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {SideNavService} from '@common/components/side-nav/side-nav.service';
import {HeadlineRoute} from '@common/components/side-nav/headline-route';
import {SelectionModel} from '@angular/cdk/collections';

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrls: ['./side-nav.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SideNavComponent implements OnInit, OnChanges {

  @Input()
  headlines: HeadlineRoute[];

  expanded = new SelectionModel<HeadlineRoute>(true);

  constructor(public sideNavService: SideNavService) {
  }

  ngOnInit(): void {
    this.setExpanded()
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.headlines) {
      this.setExpanded();
    }
  }

  setExpanded(): void {
    this.expanded.clear();
    const toExpand: HeadlineRoute[] = [];
    this.headlines.forEach(root => this.geExpandableHeadlines(root, toExpand));
    this.expanded.select(...toExpand);
  }

  private geExpandableHeadlines(headline: HeadlineRoute, expandable: HeadlineRoute[]): void {
    if (!headline?.bullets?.length) {
      return;
    }
    expandable.push(headline);
    headline.bullets.forEach(entry => this.geExpandableHeadlines(entry, expandable));
  }

}
