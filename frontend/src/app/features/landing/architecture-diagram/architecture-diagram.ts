import { Component } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

interface ServiceNode {
  icon: string;
  label: string;
}

@Component({
  selector: 'app-architecture-diagram',
  imports: [MatIcon],
  templateUrl: './architecture-diagram.html',
  styleUrl: './architecture-diagram.scss',
})
export class ArchitectureDiagram {
  protected readonly services: ServiceNode[] = [
    { icon: 'group', label: 'Customer Service' },
    { icon: 'inventory_2', label: 'Product Service' },
    { icon: 'receipt_long', label: 'Order Service' },
    { icon: 'payments', label: 'Payment Service' },
  ];
}
