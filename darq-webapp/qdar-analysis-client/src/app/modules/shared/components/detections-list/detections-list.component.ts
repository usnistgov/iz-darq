import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { IDetectionResource } from '../../model/public.model';
import { MatDialog } from '@angular/material/dialog';
import { DetectionsPickerComponent } from '../detections-picker/detections-picker.component';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-detections-list',
  templateUrl: './detections-list.component.html',
  styleUrls: ['./detections-list.component.scss']
})
export class DetectionsListComponent implements OnInit {

  @Output()
  valueChange: EventEmitter<string[]>;

  @Input()
  viewOnly: boolean;

  @Input()
  set detections(list: IDetectionResource[]) {
    this.detectionsList = list;
    this.detectionsMap = {};
    list.forEach((detection) => {
      this.detectionsMap[detection.id] = detection;
    });
  }

  unrecognizedCodes: string[];

  @Input()
  set value(list: string[]) {
    this.pValue = list;
    this.unrecognizedCodes = this.pValue.filter((code) => !this.detectionsMap[code]);
    this.filter('');
  }

  get value() {
    return this.pValue;
  }

  filtered: string[];
  detectionsList: IDetectionResource[];
  detectionsMap: {
    [code: string]: IDetectionResource;
  };
  pValue: string[];

  constructor(public dialog: MatDialog) {
    this.valueChange = new EventEmitter<string[]>();
  }

  remove(i: number) {
    this.value = [
      ...this.value.slice(0, i),
      ...this.value.slice(i + 1, this.value.length),
    ];
    this.triggerChange();
  }

  filter(text) {
    this.filtered = this.value.filter((id) => {
      const d = this.detectionsMap[id];
      return d && (d.id.includes(text) || d.target.includes(text) || d.description.includes(text));
    });
  }

  pick() {
    this.dialog.open(DetectionsPickerComponent, {
      minWidth: '95vw',
      maxHeight: '95vh',
      data: {
        detections: this.detectionsList,
        selection: this.value,
      }
    }).afterClosed().pipe(
      map((add: string[]) => {
        if (add) {
          add.forEach((id) => {
            const distinct = add.filter((elm) => {
              return !this.value.find((s) => s === elm);
            });

            if (distinct.length > 0) {
              this.value = [
                ...this.value,
                ...distinct,
              ];
              this.triggerChange();
            }

          });
        }
      })
    ).subscribe();
  }

  triggerChange() {
    this.valueChange.emit(this.value);
  }

  ngOnInit(): void {
  }

}
