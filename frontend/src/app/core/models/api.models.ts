export interface Customer {
  id: string;
  name: string;
  email: string;
  phone: string;
  address: string;
}

export type CustomerInput = Omit<Customer, 'id'>;

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  category: string;
}

export type ProductInput = Omit<Product, 'id'>;

export type OrderStatus = 'CREATED' | 'PAID' | 'CANCELLED';

export interface OrderItemLine {
  productId: string;
  quantity: number;
  unitPrice: number;
}

export interface Order {
  id: string;
  customerId: string;
  totalAmount: number;
  status: OrderStatus | string;
  items: OrderItemLine[];
}

export interface RecentOrder {
  id: string;
  customerName: string;
  itemCount: number;
  totalAmount: number;
  status: OrderStatus | string;
}

export interface OrderItem {
  productName: string;
  quantity: number;
  unitPrice: number;
}

export interface OrderDetail {
  id: string;
  customerName: string;
  items: OrderItem[];
  totalAmount: number;
  status: OrderStatus | string;
}

export interface OrderLineInput {
  productId: string;
  quantity: number;
  unitPrice: number;
}

export interface OrderInput {
  customerId: string;
  items: OrderLineInput[];
}

export type PaymentStatus = 'PENDING' | 'APPROVED' | 'DECLINED';
export type PaymentMethod = 'CREDIT_CARD' | 'PIX' | 'BOLETO';

export interface Payment {
  id: string;
  orderId: string;
  amount: number;
  status: PaymentStatus | string;
  method: PaymentMethod | string;
}

export interface DashboardSummary {
  totalCustomers: number;
  totalProducts: number;
  totalOrders: number;
  totalRevenue: number;
  recentOrders: RecentOrder[];
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}
