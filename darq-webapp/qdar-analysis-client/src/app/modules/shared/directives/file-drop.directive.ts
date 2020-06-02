import { Directive, HostListener, EventEmitter, Output } from '@angular/core';

@Directive({
  // tslint:disable-next-line: directive-selector
  selector: '[fileDrop]'
})
export class FileDropDirective {

  @Output()
  dropped: EventEmitter<File> = new EventEmitter();

  @Output()
  hover: EventEmitter<boolean> = new EventEmitter();

  constructor() {
  }

  @HostListener('mouseenter') onMouseEnter() {
  }

  @HostListener('drop', ['$event']) onDrop(event) {
    event.preventDefault();
    event.stopPropagation();
    const files = event.dataTransfer.files;
    if (files.length === 1) {
      this.dropped.emit(files[0]);
    }
  }

  @HostListener('dragover', ['$event']) dragover(event) {
    event.preventDefault();
    event.stopPropagation();
    this.hover.emit(true);
  }

  @HostListener('dragleave', ['$event']) dragleave(event) {
    event.preventDefault();
    event.stopPropagation();
    this.hover.emit(false);
  }
}
