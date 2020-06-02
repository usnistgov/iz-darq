import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { AnalysisType } from '../../../../report-template/model/analysis.values';
import { UserMessage } from 'ngx-dam-framework';

export abstract class QueryDialogTabComponent<T> implements OnInit {

  @Input()
  analysis: AnalysisType;
  @Input()
  value: T;
  @Output()
  valueChange: EventEmitter<T>;
  @Output()
  valid: EventEmitter<boolean>;
  @Output()
  messages: EventEmitter<UserMessage[]>;

  constructor() {
    this.valueChange = new EventEmitter();
    this.valid = new EventEmitter();
    this.messages = new EventEmitter();
  }

  emitChange(value: T) {
    this.valueChange.emit(value);
    this.emitValid(value);
  }

  emitValid(value: T) {
    const result = this.validate(value);
    this.valid.emit(result.status);
    this.messages.emit(result.issues);
  }

  abstract validate(value: T): { status: boolean, issues: UserMessage[] };

  ngOnInit(): void {
  }

}
