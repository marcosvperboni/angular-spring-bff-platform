import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
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
import { ProductService } from '../../core/services/product.service';
import { Product } from '../../core/models/api.models';

@Component({
  selector: 'app-products',
  imports: [
    ReactiveFormsModule,
    MatButton,
    MatIconButton,
    MatCard,
    MatCardContent,
    MatFormField,
    MatLabel,
    MatError,
    MatInput,
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
  templateUrl: './products.html',
  styleUrl: './products.scss',
})
export class Products {
  private readonly productService = inject(ProductService);
  private readonly fb = inject(FormBuilder);

  protected readonly products = signal<Product[]>([]);
  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly showForm = signal(false);
  protected readonly saving = signal(false);
  protected readonly displayedColumns = ['name', 'category', 'price', 'stockQuantity', 'actions'];

  protected readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    description: [''],
    category: [''],
    price: [0, [Validators.required, Validators.min(0.01)]],
    stockQuantity: [0, [Validators.required, Validators.min(0)]],
  });

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.productService.list().subscribe({
      next: (products) => {
        this.products.set(products);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Unable to load products.');
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
    this.productService.create(this.form.getRawValue()).subscribe({
      next: () => {
        this.saving.set(false);
        this.showForm.set(false);
        this.form.reset({ price: 0, stockQuantity: 0 });
        this.load();
      },
      error: () => {
        this.saving.set(false);
        this.errorMessage.set('Unable to create product.');
      },
    });
  }

  remove(product: Product): void {
    this.productService.delete(product.id).subscribe(() => this.load());
  }
}
