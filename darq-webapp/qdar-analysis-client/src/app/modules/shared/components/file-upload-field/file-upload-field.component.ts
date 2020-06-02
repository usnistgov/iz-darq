import { Component, OnInit, Output, EventEmitter, ViewChild, ElementRef, Input } from '@angular/core';

@Component({
  selector: 'app-file-upload-field',
  templateUrl: './file-upload-field.component.html',
  styleUrls: ['./file-upload-field.component.scss']
})
export class FileUploadFieldComponent implements OnInit {

  @Output()
  file: EventEmitter<File> = new EventEmitter();
  hover: boolean;
  @ViewChild('input')
  input: ElementRef;
  current: File;
  @Input()
  label: string;
  @Input()
  required: boolean;

  constructor() { }

  ngOnInit() {
    this.hover = false;
  }

  color() {
    if (this.current) {
      return 'lightgreen';
    }
    return '#ff756d';
  }

  clear() {
    this.current = null;
    this.input.nativeElement.value = '';
    this.file.emit(null);
    this.hover = false;
  }

  change(event) {
    const files = event.srcElement.files;
    if (files.length === 1) {
      const file = files[0];
      this.current = file;
      this.input.nativeElement.value = '';
      this.file.emit(file);
      this.hover = false;
    }
  }

  drop(file: File) {
    this.current = file;
    this.file.emit(file);
    this.hover = false;
  }

  onHover(b: boolean) {
    this.hover = b;
  }

}
