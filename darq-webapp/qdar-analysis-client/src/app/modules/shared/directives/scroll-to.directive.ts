import { Directive, ElementRef, Input } from '@angular/core';

@Directive({
  selector: '[appScrollTo]'
})
export class ScrollToDirective {

  @Input()
  nodeId: string;

  constructor(private elm: ElementRef) {
  }

  getNodeId() {
    return this.nodeId;
  }

  getY() {
    return this.elm.nativeElement.offsetTop;
  }

}
