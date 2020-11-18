import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { IADFDescriptor } from '../../model/adf.model';
import { SelectItem } from 'primeng/api/selectitem';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-adf-merge-dialog',
  templateUrl: './adf-merge-dialog.component.html',
  styleUrls: ['./adf-merge-dialog.component.scss']
})
export class AdfMergeDialogComponent implements OnInit {

  files: IADFDescriptor[];
  filtered$: BehaviorSubject<IADFDescriptor[]>;
  facilities: SelectItem[];
  facilityMap: Record<string, string> = {};
  fileSelection: IADFDescriptor[] = [];

  name: string;
  facilityId: string;

  constructor(
    public dialogRef: MatDialogRef<AdfMergeDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
    this.files = data.files;
    this.filtered$ = new BehaviorSubject(this.files);
    this.facilities = data.facilities.map((f) => {
      this.facilityMap[f.id] = f.name;
      return {
        label: f.name,
        value: f.id,
      };
    });
    this.name = '';
    this.facilityId = '';
  }

  selectionChanged(selection: IADFDescriptor[]) {
    console.log(selection);
    if (selection && selection.length > 0) {
      const version = selection[0].version;
      if (version) {
        this.filtered$.next(this.files.filter((file) => {
          return file.version === version;
        }));
      } else {
        this.filtered$.next(selection);
      }
    } else {
      this.filtered$.next(this.files);
    }
  }

  valid() {
    return this.name && this.facilityId && this.fileSelection && this.fileSelection.length > 1;
  }

  merge() {
    this.dialogRef.close({
      name: this.name,
      facilityId: this.facilityId,
      ids: this.fileSelection.map((f) => f.id),
    });
  }

  ngOnInit(): void {
  }

}
