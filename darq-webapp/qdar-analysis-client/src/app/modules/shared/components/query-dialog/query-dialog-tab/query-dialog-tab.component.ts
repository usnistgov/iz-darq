import { OnInit, Input, Output, EventEmitter, Component } from '@angular/core';
import { AnalysisType, fieldDisplayName } from '../../../../report-template/model/analysis.values';
import { UserMessage } from '@usnistgov/ngx-dam-framework-legacy';
@Component({
  template: ''
})
export abstract class QueryDialogTabComponent<T> implements OnInit {

  @Input()
  set analysis(type: AnalysisType) {
    this.type = type;
  }
  get analysis() {
    return this.type;
  }
  type: AnalysisType;
  @Input()
  value: T;
  @Output()
  valueChange: EventEmitter<T>;
  @Output()
  valid: EventEmitter<boolean>;
  @Output()
  messages: EventEmitter<UserMessage[]>;
  fieldDisplayName = fieldDisplayName;

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
