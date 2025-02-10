import { Component, OnInit, Input, TemplateRef } from '@angular/core';
import { IWorkspaceActive } from '@usnistgov/ngx-dam-framework-legacy';

@Component({
  selector: 'app-rt-active-title',
  templateUrl: './rt-active-title.component.html',
  styleUrls: ['./rt-active-title.component.scss']
})
export class RtActiveTitleComponent implements OnInit {

  @Input()
  active: IWorkspaceActive;
  @Input()
  controls: TemplateRef<any>;

  constructor() {
  }

  ngOnInit(): void {
  }

}
