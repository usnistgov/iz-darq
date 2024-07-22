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
import { MatSidenavModule } from '@angular/material/sidenav';
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
import { AccessResourcePipe } from './pipes/access-resource.pipe';
import { AccessScopePipe } from './pipes/access-scope.pipe';
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { MustMatchDirective } from './directives/must-match.directive';
import { PeAutoscrollFixDirective } from './directives/pe-auto-scroll-fix.directive';
import { CustomLabelDialogComponent } from './components/custom-label-dialog/custom-label-dialog.component';
import { QueryOccurrencesComponent } from './components/query-dialog/query-occurrences/query-occurrences.component';
import { CheckboxModule } from 'primeng/checkbox';
import { SimpleQueryComponent } from './components/query-dialog/simple-query/simple-query.component';
import { TreeTableModule } from 'primeng/treetable';
import { MultiFieldInputComponent } from './components/multi-field-input/multi-field-input.component';
import { QueryListDialogComponent } from './components/query-list-dialog/query-list-dialog.component';
import { QuerySaveDialogComponent } from './components/query-save-dialog/query-save-dialog.component';
import { RadioButtonModule } from 'primeng/radiobutton';
import { VariableSelectDialogComponent } from './components/variable-select-dialog/variable-select-dialog.component';
import { VariableRefDisplayComponent } from './components/variable-ref-display/variable-ref-display.component';
import { VariableInstanceDisplayComponent } from './components/variable-instance-display/variable-instance-display.component';
import { QueryDenominatorOverrideComponent } from './components/query-dialog/query-denominator-override/query-denominator-override.component';
import { VariableRefComponent } from './components/variable-ref/variable-ref.component';
import { VariableQueryComponent } from './components/query-dialog/variable-query/variable-query.component';
import { ChipsModule } from 'primeng/chips';

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
    AccessResourcePipe,
    AccessScopePipe,
    UserProfileComponent,
    MustMatchDirective,
    PeAutoscrollFixDirective,
    CustomLabelDialogComponent,
    QueryOccurrencesComponent,
    SimpleQueryComponent,
    MultiFieldInputComponent,
    QueryListDialogComponent,
    QuerySaveDialogComponent,
    VariableSelectDialogComponent,
    VariableRefDisplayComponent,
    VariableInstanceDisplayComponent,
    QueryDenominatorOverrideComponent,
    VariableRefComponent,
    VariableQueryComponent,
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
    MatSidenavModule,
    TabViewModule,
    DragDropModule,
    AccordionModule,
    TooltipModule,
    CheckboxModule,
    TreeTableModule,
    RadioButtonModule,
    ChipsModule,
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
    TreeTableModule,
    CheckboxModule,
    RadioButtonModule,
    ChipsModule,
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
    AccessResourcePipe,
    AccessScopePipe,
    UserProfileComponent,
    MustMatchDirective,
    PeAutoscrollFixDirective,
    CustomLabelDialogComponent,
    QueryListDialogComponent,
  ],
  entryComponents: [CustomLabelDialogComponent]
})
export class SharedModule { }
