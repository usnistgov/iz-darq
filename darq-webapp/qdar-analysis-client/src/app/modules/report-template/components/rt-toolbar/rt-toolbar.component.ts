import { Component, OnInit, Input, TemplateRef } from '@angular/core';
import { Observable } from 'rxjs';
import { Store } from '@ngrx/store';
import { selectPayloadData } from 'ngx-dam-framework';
import { pluck } from 'rxjs/operators';

@Component({
  selector: 'app-rt-toolbar',
  templateUrl: './rt-toolbar.component.html',
  styleUrls: ['./rt-toolbar.component.scss']
})
export class RtToolbarComponent implements OnInit {

  isViewOnly$: Observable<boolean>;
  @Input()
  controls: TemplateRef<any>;

  constructor(
    private store: Store<any>,
  ) {
    this.isViewOnly$ = this.store.select(selectPayloadData).pipe(pluck('viewOnly'));
  }

  ngOnInit(): void {
  }

}
