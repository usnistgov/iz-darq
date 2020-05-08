import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";


export class FileDownload {
	id : string;
	name: string;
	description: string;
	size: string;
	path: string;
}

@Injectable()
export class DownloadService {

	constructor(private http: HttpClient) {
	}

	async catalog() {
		return await this.http.get<FileDownload[]>("/public/downloads").toPromise();
	}

}

