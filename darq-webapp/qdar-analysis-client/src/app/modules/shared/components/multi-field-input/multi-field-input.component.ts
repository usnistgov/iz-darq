import { IValueContainer } from './../../../report-template/model/report-template.model';
import { AnalysisType, Field } from 'src/app/modules/report-template/model/analysis.values';
import { IFieldInputOptions } from 'src/app/modules/shared/components/field-input/field-input.component';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { Component, OnInit, forwardRef, Input } from '@angular/core';

@Component({
  selector: 'app-multi-field-input',
  templateUrl: './multi-field-input.component.html',
  styleUrls: ['./multi-field-input.component.scss'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => MultiFieldInputComponent),
    multi: true
  }]
})
export class MultiFieldInputComponent implements ControlValueAccessor, OnInit {

  @Input()
  options: IFieldInputOptions;
  @Input()
  showClear: boolean;
  @Input()
  placeholder: string;
  @Input()
  field: Field;
  @Input()
  id: string;
  @Input()
  analysis: AnalysisType;
  onChange: any;
  onTouched: any;
  disabled: boolean;
  value: any;

  constructor() { }

  valueListToValueContainerList(values: string[]): IValueContainer[] {
    if (values) {
      return values.map((v) => ({ value: v }));
    } else {
      return undefined;
    }
  }

  valueContainerListToValueList(values: IValueContainer[]): string[] {
    if (values) {
      return values.map((v) => v.value);
    } else {
      return undefined;
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  writeValue(obj: IValueContainer[]): void {
    if (obj) {
      this.value = this.valueContainerListToValueList(obj);
    }
  }

  valueChange(value) {
    this.onChange(this.valueListToValueContainerList(value));
  }

  ngOnInit(): void {
  }

}
