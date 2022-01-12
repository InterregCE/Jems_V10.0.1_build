import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LumpsumBudgetTableComponent } from './lumpsum-budget-table.component';
import {ProjectPartnerDetailPageStore} from '@project/partner/project-partner-detail-page/project-partner-detail-page.store';
import {TestModule} from '@common/test-module';
import {ProjectModule} from '@project/project.module';

describe('LumpsumBudgetTableComponent', () => {
  let component: LumpsumBudgetTableComponent;
  let fixture: ComponentFixture<LumpsumBudgetTableComponent>;
  let partnerDetailPageStore: ProjectPartnerDetailPageStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        TestModule,
        ProjectModule
      ],
      declarations: [ LumpsumBudgetTableComponent ],
      providers: [
        {
          provide: ProjectPartnerDetailPageStore,
          useClass: ProjectPartnerDetailPageStore
        }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LumpsumBudgetTableComponent);
    partnerDetailPageStore = TestBed.inject(ProjectPartnerDetailPageStore);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
