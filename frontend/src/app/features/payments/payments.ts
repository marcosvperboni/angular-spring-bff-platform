import { Component, inject, signal } from '@angular/core';
import { MatCard, MatCardContent } from '@angular/material/card';
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
import { PaymentService } from '../../core/services/payment.service';
import { Payment } from '../../core/models/api.models';

@Component({
  selector: 'app-payments',
  imports: [
    MatCard,
    MatCardContent,
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
  templateUrl: './payments.html',
  styleUrl: './payments.scss',
})
export class Payments {
  private readonly paymentService = inject(PaymentService);

  protected readonly payments = signal<Payment[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly displayedColumns = ['id', 'orderId', 'amount', 'method', 'status'];

  constructor() {
    this.paymentService.list().subscribe({
      next: (payments) => {
        this.payments.set(payments);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Unable to load payments.');
        this.loading.set(false);
      },
    });
  }
}
