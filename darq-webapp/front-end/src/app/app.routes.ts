import {LoginComponent} from "./components/content/login/login.component";
import {HomeComponent} from "./components/content/home/home.component";
import {
	DetectionsListResolver, ConfigurationCatalogResolver,
	ConfigurationResolver, CVXListResolver, DownloadResolver
} from "./resolvers/configuration.resolver";
import {AuthGuard} from "./guards/auth.guard";
import {DataComponent} from "./components/content/data/data.component";
import {UploadComponent} from "./components/content/upload/upload.component";
import {ADFSummaryComponent} from "./components/content/adf-summary/adf-summary.component";
import {ADFResolver, ADFListResolver} from "./resolvers/adf.resolver";
import {ExtractConfigurationComponent} from "./components/content/extract-configuration/extract-configuration.component";
import {TemplateComponent} from "./components/content/template/template.component";
import {
	PatientCodeSetResolver,
	TemplateCatalogResolver,
	TemplateResolver,
	VaccineCodeSetResolver
} from "./resolvers/template.resolver";
import {ReportComponent} from "./components/content/report/report.component";
import {DownloadComponent} from "./components/content/download/download.component";


export const ROUTES = [
	{
		path: "login",
		component: LoginComponent
	},
	{
		path: "download",
		component: DownloadComponent,
		resolve : {
			catalog : DownloadResolver
		}
	},
	{
		path : "data",
		canActivate: [AuthGuard],
		children : [
			{
				path : "upload",
				component : UploadComponent
			},
			{
				path : "files",
				children : [
					{
						path : "",
						component : DataComponent,
						resolve: {
							files : ADFListResolver
						}
					},
					{
						path : ":fid",
						children : [
							{
								path : "summary",
								component : ADFSummaryComponent,
								resolve : {
									file : ADFResolver,
									detections : DetectionsListResolver,
									cvx : CVXListResolver
								}
							}
						]
					},
				]
			},
			{
				path: "",
				redirectTo: "files",
				pathMatch: "prefix"
			},

		]
	},
	{
		path : "report/:tId/:fId",
		canActivate: [AuthGuard],
		component : ReportComponent,
		resolve: {
			detections : DetectionsListResolver,
			cvx : CVXListResolver
		}
	},
	{
		path: "home",
		component: HomeComponent
	},
	{
		path : "report-templates",
		canActivate: [AuthGuard],
		children: [
			{
				path: "",
				redirectTo: "new",
				pathMatch: "prefix"
			},
			{
				path: ":ctx",
				component : TemplateComponent,
				resolve: {
					configCatalog : ConfigurationCatalogResolver,
					catalog : TemplateCatalogResolver,
					template : TemplateResolver,
					detectionList : DetectionsListResolver,
					pCodeSet : PatientCodeSetResolver,
					vCodeSet : VaccineCodeSetResolver,
					cvx : CVXListResolver
				}
			}
		]
	},
	{
		path: "configuration",
		canActivate: [AuthGuard],
		children: [
			{
				path: "",
				redirectTo: "new",
				pathMatch: "prefix"
			},
			{
				path: ":ctx",
				component: ExtractConfigurationComponent,
				resolve: {
					detections : DetectionsListResolver,
					catalog : ConfigurationCatalogResolver,
					configuration : ConfigurationResolver,
					cvx : CVXListResolver
				}
			}
		]
	},
	{
		path: "",
		pathMatch: "full",
		redirectTo: "home"
	}
];
