import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/structural/header/header.component';
import {FooterComponent} from './components/structural/footer/footer.component';
import {LoginComponent} from './components/content/login/login.component';
import {RouterModule} from "@angular/router";
import {HomeComponent} from './components/content/home/home.component';
import {UserService} from "./services/user.service";
import {FlexLayoutModule} from "@angular/flex-layout";
import {ServerInfoService} from "./services/server-info.service";
import {AuthInterceptor} from "./services/auth.interceptor";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AuthGuard} from "./guards/auth.guard";
import {BsDropdownModule} from "ngx-bootstrap";
import {FileComponentComponent} from './components/fragments/file-component/file-component.component';
import {FileDropDirective} from './directives/file-drop.directive';
import {TreeModule} from "primeng/components/tree/tree";
import {BlockUIModule} from "ng-block-ui";
import {MaterialModule} from "./material.module";
import {PanelMenuModule} from "primeng/components/panelmenu/panelmenu";
import {SideNavMenuModule} from "mat-sidenav-menu";
import {NgxChartsModule} from "@swimlane/ngx-charts";
import {ChartFilterPipe} from "./pipes/chart-filter.pipe";
import {ConfigurationService} from "./services/configuration.service";
import {TreeTableModule} from "primeng/components/treetable/treetable";
import {DropdownModule} from "primeng/components/dropdown/dropdown";
import {TableModule} from "primeng/components/table/table";
import {ROUTES} from "./app.routes";
import {
	ConfigurationResolver, ConfigurationCatalogResolver, DetectionsListResolver, CVXListResolver, DownloadResolver
} from "./resolvers/configuration.resolver";
import {HttpModule} from "@angular/http";
import {DataComponent} from "./components/content/data/data.component";
import {UploadComponent} from "./components/content/upload/upload.component";
import {ADFSummaryComponent} from "./components/content/adf-summary/adf-summary.component";
import {ADFResolver, ADFListResolver} from "./resolvers/adf.resolver";
import {AdfService} from "./services/adf.service";
import {ExtractConfigurationComponent} from "./components/content/extract-configuration/extract-configuration.component";
import {AgeGroupComponent} from "./components/fragments/age-group/age-group.component";
import {RangesService} from "./services/ranges.service";
import {TemplateComponent} from "./components/content/template/template.component";
import {ReportSectionComponent} from "./components/fragments/report-section/report-section.component";
import {AnalysisPayloadDialogComponent} from "./components/fragments/analysis-payload-dialog/analysis-payload-dialog.component";
import {MAT_DIALOG_DEFAULT_OPTIONS, MatDatepickerModule} from "@angular/material";
import {TemplateFilterPipe} from "./pipes/template-filter.pipe";
import {TemplateService} from "./services/template.service";
import {
	PatientCodeSetResolver,
	TemplateCatalogResolver,
	TemplateResolver,
	VaccineCodeSetResolver
} from "./resolvers/template.resolver";
import {DndListModule} from "ngx-drag-and-drop-lists";
import {NgSelectModule} from "@ng-select/ng-select";
import {AnalysisDialogComponent} from "./components/fragments/analysis-dialog/analysis-dialog.component";
import {ReportComponent} from "./components/content/report/report.component";
import {ChartsModule} from "ng2-charts";
import {VarDirective} from "./directives/var.directive";
import {Ng2GoogleChartsModule} from "ng2-google-charts";
import {AutoCompleteModule} from "primeng/components/autocomplete/autocomplete";
import {DownloadComponent} from "./components/content/download/download.component";
import {DownloadService} from "./services/download.service";
import {NotifierModule, NotifierService} from "angular-notifier";
import {ConfirmDialogComponent} from "./components/fragments/confirm-dialog/confirm-dialog.component";
import {DataTableComponent} from "./components/fragments/data-table/data-table.component";
import {AngularResizedEventModule} from "angular-resize-event";
import {CommonModule, DatePipe} from "@angular/common";
import {CalendarModule} from "primeng/primeng";


@NgModule({
	declarations: [
		AppComponent,
		HeaderComponent,
		FooterComponent,
		LoginComponent,
		HomeComponent,
		DataTableComponent,
		ADFSummaryComponent,
		FileComponentComponent,
		FileDropDirective,
		DataComponent,
		VarDirective,
		UploadComponent,
		ChartFilterPipe,
		DownloadComponent,
		TemplateFilterPipe,
		AgeGroupComponent,
		ExtractConfigurationComponent,
		TemplateComponent,
		ReportSectionComponent,
		AnalysisPayloadDialogComponent,
		AnalysisDialogComponent,
		ReportComponent,
		ConfirmDialogComponent
	],
	imports: [
		BrowserModule,
		RouterModule.forRoot(ROUTES, {useHash: true, onSameUrlNavigation: 'reload'}),
		FormsModule,
		ReactiveFormsModule,
		CommonModule,
		FlexLayoutModule,
		CalendarModule,
		BsDropdownModule.forRoot(),
		MaterialModule,
		NgxChartsModule,
		PanelMenuModule,
		AutoCompleteModule,
		SideNavMenuModule,
		ChartsModule,
		TreeTableModule,
		MatDatepickerModule,
		DropdownModule,
		TableModule,
		NgSelectModule,
		DndListModule,
		AngularResizedEventModule,
		BlockUIModule,
		Ng2GoogleChartsModule,
		HttpModule,
		TreeModule,
		NotifierModule.withConfig( {
			position : {
				horizontal : {
					position : 'right'
				},
				vertical : {
					position : 'top'
				}
			}
		}),
		HttpClientModule
	],
	providers: [
		ServerInfoService,
		UserService,
		TemplateService,
		TemplateCatalogResolver,
		PatientCodeSetResolver,
		VaccineCodeSetResolver,
		TemplateResolver,
		AuthGuard,
		RangesService,
		ConfigurationService,
		ConfigurationResolver,
		ADFResolver,
		ADFListResolver,
		DetectionsListResolver,
		CVXListResolver,
		DownloadService,
		DownloadResolver,
		AdfService,
		NotifierService,
		ConfigurationCatalogResolver,
		DatePipe,
		{
			provide: HTTP_INTERCEPTORS,
			useClass: AuthInterceptor,
			multi: true
		}
	],
	entryComponents : [
		AnalysisPayloadDialogComponent,
		AnalysisDialogComponent,
		ConfirmDialogComponent
	],
	bootstrap: [AppComponent]
})
export class AppModule {
}
