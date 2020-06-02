import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AgeGroupsComponent } from './components/age-groups/age-groups.component';
import { AgeBracketComponent } from './components/age-bracket/age-bracket.component';
import { DetectionsListComponent } from './components/detections-list/detections-list.component';
import { TableModule } from 'primeng/table';
import { MultiSelectModule } from 'primeng/multiselect';
import { DetectionsPickerComponent } from './components/detections-picker/detections-picker.component';
import { ContextMenuModule } from 'ngx-contextmenu';
import { EffectsModule } from '@ngrx/effects';
import { CoreEffects } from './store/core.effects';
import { EditorModule } from 'primeng/editor';
import { CalendarModule } from 'primeng/calendar';
import { DamMessagesModule } from 'ngx-dam-framework';
import { DropdownModule } from 'primeng/dropdown';
import { FileUploadFieldComponent } from './components/file-upload-field/file-upload-field.component';
import { FileDropDirective } from './directives/file-drop.directive';
import { DescriptorDisplayComponent } from './components/descriptor-display/configuration-display.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TreeModule } from 'angular-tree-component';
import { MatDialogModule } from '@angular/material/dialog';
import { QueryDialogComponent } from './components/query-dialog/query-dialog.component';
import { GeneralQueryDataComponent } from './components/query-dialog/general-query-data/general-query-data.component';
import { QuerySelectorComponent } from './components/query-dialog/query-selector/query-selector.component';
import { TabViewModule } from 'primeng/tabview';
import { QueryGroupComponent } from './components/query-dialog/query-group/query-group.component';
import { QueryThresholdComponent } from './components/query-dialog/query-threshold/query-threshold.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { FieldInputComponent } from './components/field-input/field-input.component';
import { PostFiltersComponent } from './components/query-dialog/post-filters/post-filters.component';
import { AccordionModule } from 'primeng/accordion';
import { DataTableComponent } from './components/data-table/data-table.component';
import { VisualBarComponent } from './components/visual-bar/visual-bar.component';
import { TooltipModule } from 'primeng/tooltip';
import { QueryDisplayComponent } from './components/query-display/query-display.component';
import { DataTableDialogComponent } from './components/data-table-dialog/data-table-dialog.component';
import { ScrollToDirective } from './directives/scroll-to.directive';
import { NameDialogComponent } from './components/name-dialog/name-dialog.component';

@NgModule({
  declarations: [
    AgeGroupsComponent,
    AgeBracketComponent,
    DetectionsListComponent,
    DetectionsPickerComponent,
    FileUploadFieldComponent,
    FileDropDirective,
    DescriptorDisplayComponent,
    QueryDialogComponent,
    GeneralQueryDataComponent,
    QuerySelectorComponent,
    QueryGroupComponent,
    QueryThresholdComponent,
    FieldInputComponent,
    PostFiltersComponent,
    DataTableComponent,
    VisualBarComponent,
    QueryDisplayComponent,
    DataTableDialogComponent,
    ScrollToDirective,
    NameDialogComponent,
  ],
  imports: [
    CommonModule,
    StoreModule,
    FormsModule,
    ReactiveFormsModule,
    TableModule,
    MultiSelectModule,
    ContextMenuModule,
    EffectsModule.forFeature([CoreEffects]),
    EditorModule,
    CalendarModule,
    DamMessagesModule,
    DropdownModule,
    NgbModule,
    TreeModule,
    MatDialogModule,
    TabViewModule,
    DragDropModule,
    AccordionModule,
    TooltipModule,
  ],
  exports: [
    // Modules,
    StoreModule,
    FormsModule,
    ReactiveFormsModule,
    TableModule,
    MultiSelectModule,
    ContextMenuModule,
    EditorModule,
    CalendarModule,
    DamMessagesModule,
    DropdownModule,
    NgbModule,
    TreeModule,
    MatDialogModule,
    DragDropModule,
    AccordionModule,
    TooltipModule,
    TabViewModule,
    // Components,
    AgeGroupsComponent,
    DetectionsListComponent,
    DetectionsPickerComponent,
    FileUploadFieldComponent,
    FileDropDirective,
    DescriptorDisplayComponent,
    QueryDialogComponent,
    GeneralQueryDataComponent,
    QuerySelectorComponent,
    DataTableComponent,
    VisualBarComponent,
    QueryDisplayComponent,
    DataTableDialogComponent,
    ScrollToDirective,
    FieldInputComponent,
  ]
})
export class SharedModule { }
