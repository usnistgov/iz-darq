import {Component, OnInit, Output, EventEmitter, Input} from '@angular/core';
import {Barket, Range} from "../../../domain/summary";
import {RangesService} from "../../../services/ranges.service";

@Component({
	selector: 'app-age-group',
	templateUrl: 'age-group.component.html',
	styleUrls: ['age-group.component.css']
})
export class AgeGroupComponent implements OnInit {

	@Output('change') change: EventEmitter<Range[]> = new EventEmitter<Range[]>();
	stub: Barket;
	barkets: Barket[];
	edit : boolean;

	constructor(private $range : RangesService) {
		this.stub = new Barket();
	}

	@Input() set rangeList(rs : Range[]){
		if(rs instanceof Array){
			this.barkets = [];
			for(let r of rs){
				this.barkets.push(r.max);
			}
		}
	}

	@Input() set editable(e : boolean){
		this.edit = e;
	}

	sort() {
		this.barkets.sort((a: Barket, b: Barket) => {
			let dec = a.year * 10000 + a.month * 100 + a.day * 10;
			let odec = b.year * 10000 + b.month * 100 + b.day * 10;
			return dec - odec;
		});
	}

	add(n : Barket){
		let found = false;
		let copy = {
			year : n.year,
			month : n.month,
			day : n.day
		};

		for(let b of this.barkets){
			if(b.year === n.year && b.month === n.month && b.day === n.day)
				found = true;
		}

		if(!found && (n.year + n.month + n.day) != 0){
			this.barkets.push(copy);
			n = { year : 0, month : 0, day : 0};
		}
		this.sort();
		this.change.emit(this.groups());
	}

	remove(i : number){
		this.barkets.splice(i, 1);
		this.change.emit(this.groups());
	}

	groups(){
		let start : Barket = { day : 0, year : 0, month : 0};
		let ranges : Range[] = [];
		for(let b of this.barkets){
			ranges.push({min : start, max : b});
			start = b;
		}
		return ranges;
	}

	barket(b : Barket){
		return this.$range.barket(b);
	}

	max(){
		return this.$range.barket(this.barkets[this.barkets.length - 1]);
	}

	ngOnInit() {
	}

}
