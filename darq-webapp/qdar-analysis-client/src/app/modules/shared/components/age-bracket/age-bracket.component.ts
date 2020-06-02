import { Component, OnInit, Input } from '@angular/core';
import { IBracket } from '../../model/age-group.model';
import { AgeGroupService } from '../../services/age-group.service';

@Component({
  selector: 'app-age-bracket',
  templateUrl: './age-bracket.component.html',
  styleUrls: ['./age-bracket.component.scss']
})
export class AgeBracketComponent implements OnInit {

  @Input()
  bracket: IBracket;

  constructor(public ageGroupService: AgeGroupService) { }

  ngOnInit(): void {
  }

}
