import { Component, OnInit, Input, EventEmitter, Output, ViewChild, AfterViewInit, OnDestroy } from '@angular/core';
import { SelectItem } from 'primeng/api/selectitem';
import { IDataViewQuery } from '../../../../report-template/model/report-template.model';
import { NgForm, FormGroup, FormControl, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { QueryDialogTabComponent } from '../query-dialog-tab/query-dialog-tab.component';
import { UserMessage } from 'ngx-dam-framework';

@Component({
  selector: 'app-general-query-data',
  templateUrl: './general-query-data.component.html',
  styleUrls: ['./general-query-data.component.scss']
})
export class GeneralQueryDataComponent extends QueryDialogTabComponent<IDataViewQuery> implements OnInit, OnDestroy, AfterViewInit {

  @Input()
  paths: SelectItem[];
  @ViewChild('form', { static: true })
  form: NgForm;
  formGroup: FormGroup;
  vSub: Subscription;

  constructor() {
    super();
  }

  validate(value): { status: boolean; issues: UserMessage<any>[]; } {
    return {
      status: this.form.valid,
      issues: [],
    };
  }

  ngOnInit(): void {
    this.vSub = this.form.valueChanges.pipe(
      map((value) => {
        this.emitChange(this.value);
      }),
    ).subscribe();
  }

  ngAfterViewInit() {
    this.emitValid(this.value);
  }

  ngOnDestroy() {
    if (this.vSub) {
      this.vSub.unsubscribe();
    }
  }
}
