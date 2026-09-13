import { Component, inject, signal } from '@angular/core';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
} from '@angular/material/table';
import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardSummary } from '../../core/models/api.models';

interface Kpi {
  icon: string;
  label: string;
  value: () => string;
}

@Component({
  selector: 'app-dashboard',
  imports: [
    MatCard,
    MatCardContent,
    MatIcon,
    MatProgressSpinner,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCell,
    MatCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard {
  private readonly dashboardService = inject(DashboardService);

  protected readonly summary = signal<DashboardSummary | null>(null);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly displayedColumns = ['id', 'customerName', 'itemCount', 'totalAmount', 'status'];

  protected readonly kpis: Kpi[] = [
    { icon: 'group', label: 'Customers', value: () => `${this.summary()?.totalCustomers ?? 0}` },
    { icon: 'inventory_2', label: 'Products', value: () => `${this.summary()?.totalProducts ?? 0}` },
    { icon: 'receipt_long', label: 'Orders', value: () => `${this.summary()?.totalOrders ?? 0}` },
    {
      icon: 'payments',
      label: 'Revenue',
      value: () => `$${(this.summary()?.totalRevenue ?? 0).toLocaleString()}`,
    },
  ];

  constructor() {
    this.dashboardService.getSummary().subscribe({
      next: (summary) => {
        this.summary.set(summary);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Unable to load dashboard data.');
        this.loading.set(false);
      },
    });
  }
}
