import { Component, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelect } from '@angular/material/select';
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
import { CustomerService } from '../../core/services/customer.service';
import { OrderService } from '../../core/services/order.service';
import { ProductService } from '../../core/services/product.service';
import { Customer, Order, OrderDetail, Product } from '../../core/models/api.models';

@Component({
  selector: 'app-orders',
  imports: [
    ReactiveFormsModule,
    MatButton,
    MatIconButton,
    MatCard,
    MatCardContent,
    MatFormField,
    MatLabel,
    MatInput,
    MatSelect,
    MatOption,
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
  templateUrl: './orders.html',
  styleUrl: './orders.scss',
})
export class Orders {
  private readonly orderService = inject(OrderService);
  private readonly customerService = inject(CustomerService);
  private readonly productService = inject(ProductService);
  private readonly fb = inject(FormBuilder);

  protected readonly orders = signal<Order[]>([]);
  protected readonly customers = signal<Customer[]>([]);
  protected readonly products = signal<Product[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly showForm = signal(false);
  protected readonly saving = signal(false);
  protected readonly displayedColumns = ['id', 'customerId', 'items', 'totalAmount', 'status', 'actions'];

  protected readonly selectedOrder = signal<OrderDetail | null>(null);
  protected readonly detailLoading = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    customerId: ['', Validators.required],
    items: this.fb.array([this.buildItem()]),
  });

  constructor() {
    this.load();
    this.customerService.list().subscribe((customers) => this.customers.set(customers));
    this.productService.list().subscribe((products) => this.products.set(products));
  }

  protected get items(): FormArray {
    return this.form.controls.items;
  }

  private buildItem() {
    return this.fb.nonNullable.group({
      productId: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      unitPrice: [0, [Validators.required, Validators.min(0.01)]],
    });
  }

  addItem(): void {
    this.items.push(this.buildItem());
  }

  removeItem(index: number): void {
    if (this.items.length > 1) {
      this.items.removeAt(index);
    }
  }

  onProductSelected(index: number, productId: string): void {
    const product = this.products().find((p) => p.id === productId);
    if (product) {
      this.items.at(index).patchValue({ unitPrice: product.price });
    }
  }

  private load(): void {
    this.loading.set(true);
    this.orderService.list().subscribe({
      next: (orders) => {
        this.orders.set(orders);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Unable to load orders.');
        this.loading.set(false);
      },
    });
  }

  toggleForm(): void {
    this.showForm.update((value) => !value);
  }

  submit(): void {
    if (this.form.invalid || this.saving()) {
      return;
    }
    this.saving.set(true);
    this.orderService.create(this.form.getRawValue()).subscribe({
      next: () => {
        this.saving.set(false);
        this.showForm.set(false);
        this.form.reset({ customerId: '' });
        this.items.clear();
        this.items.push(this.buildItem());
        this.load();
      },
      error: () => {
        this.saving.set(false);
        this.errorMessage.set('Unable to create order. Check the customer and products selected.');
      },
    });
  }

  viewDetail(order: Order): void {
    this.detailLoading.set(true);
    this.selectedOrder.set(null);
    this.orderService.detail(order.id).subscribe({
      next: (detail) => {
        this.selectedOrder.set(detail);
        this.detailLoading.set(false);
      },
      error: () => {
        this.detailLoading.set(false);
        this.errorMessage.set('Unable to load order detail.');
      },
    });
  }

  closeDetail(): void {
    this.selectedOrder.set(null);
  }
}
