import {Directive, HostListener, EventEmitter, Output} from '@angular/core';

@Directive({
	selector: '[fileDrop]'
})
export class FileDropDirective {

	@Output()
	dropped : EventEmitter<File> = new EventEmitter();

	@Output()
	hover : EventEmitter<boolean> = new EventEmitter();

	constructor() {
	}

	@HostListener('mouseenter') onMouseEnter() {
		console.log("MOUSE ENTER");
	}

	@HostListener('drop',['$event']) onDrop(event) {
		event.preventDefault();
		event.stopPropagation();
		let files = event.dataTransfer.files;
		if (files.length == 1) {
			this.dropped.emit(files[0]);
		}
	}

	@HostListener('dragover',['$event']) dragover(event) {
		event.preventDefault();
		event.stopPropagation();
		this.hover.emit(true);
	}

	@HostListener('dragleave',['$event']) dragleave(event) {
		event.preventDefault();
		event.stopPropagation();
		this.hover.emit(false);
	}
}
