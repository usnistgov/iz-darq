import {Directive, HostListener, Input} from '@angular/core';

@Directive({
	selector: '[appExternalDrop]'
})
export class ExternalDropDirective {

	@Input()
	value : string;

	constructor() {
	}

	@HostListener('dragstart', ['$event'])
	dragStart(event) {
		event.dataTransfer.setData('text', "XXX");

	}

	@HostListener('drag', ['$event'])
	drag(event) {
		event.preventDefault();
		event.stopPropagation();
	}

	@HostListener('dragend', ['$event'])
	dragEnd(event) {
		event.preventDefault();
		event.stopPropagation();
		console.log("DRAG");
	}

	@HostListener('mouseover', ['$event'])
	mouseover(event) {
		event.preventDefault();
		event.stopPropagation();
		console.log("DRAG");
	}

	@HostListener('mouseleave', ['$event'])
	mouseleave(event) {
		event.preventDefault();
		event.stopPropagation();
		console.log("DRAG");
	}

	@HostListener('dragstart', ['$event'])
	dragstart(event) {
		event.preventDefault();
		event.stopPropagation();
		console.log("DRAG-START");
		event.dataTransfer = "ABCD";
	}

}
