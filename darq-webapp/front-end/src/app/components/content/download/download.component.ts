import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {FileDownload} from "../../../services/download.service";

@Component({
	selector: 'app-download',
	templateUrl: './download.component.html',
	styleUrls: ['./download.component.css']
})
export class DownloadComponent implements OnInit {

	catalog : FileDownload[];

	constructor(private route: ActivatedRoute, private router: Router) {
		this.catalog = [];
	}

	ngOnInit() {
		let ctrl = this;
		this.route.data.subscribe( data => {
			ctrl.catalog = data['catalog'];
		});
	}

}
