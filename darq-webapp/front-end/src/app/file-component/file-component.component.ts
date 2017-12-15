import {Component, OnInit, HostListener, Output, EventEmitter, ViewChild, ElementRef, Input} from '@angular/core';
import {FileDropDirective} from "../file-drop.directive";

@Component({
	selector: 'app-file-component',
	templateUrl: './file-component.component.html',
	styleUrls: ['./file-component.component.css']
})
export class FileComponentComponent implements OnInit {

	@Output()
	file : EventEmitter<File> = new EventEmitter();
	hover : boolean;
	@ViewChild('input')
	input : ElementRef;
	current : File;
	@Input()
	label : string;

	constructor() {}

	ngOnInit() {
		this.hover = false;
	}

	clear(){
		this.current = null;
		this.input.nativeElement.value = "";
		this.file.emit(null);
		this.hover = false;
	}

	change(event) {
		let files = event.srcElement.files;
		if(files.length == 1){
			let file = files[0];
			this.current = file;
			this.input.nativeElement.value = "";
			this.file.emit(file);
			this.hover = false;
		}
	}

	drop(file : File){
		this.current = file;
		this.file.emit(file);
		this.hover = false;
	}

	onHover(b : boolean){
		this.hover = b;
	}



}
