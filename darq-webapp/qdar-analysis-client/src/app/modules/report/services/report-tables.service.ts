import { DataTableComponent } from '../../shared/components/data-table/data-table.component';

export class ReportTablesService {
  tables: Record<string, DataTableComponent> = {};

  public addTable(id: string, table: DataTableComponent) {
    this.tables = {
      ...this.tables,
      [id]: table,
    };
  }

  public removeTable(id: string) {
    delete this.tables[id];
  }

  public getTables(): DataTableComponent[] {
    return Object.values(this.tables);
  }
}
