import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-configuration-loading-dialog',
  templateUrl: './configuration-loading-dialog.component.html',
  styleUrls: ['./configuration-loading-dialog.component.scss']
})
export class ConfigurationLoadingDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ConfigurationLoadingDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {
  }

  ngOnInit(): void {
  }

}
