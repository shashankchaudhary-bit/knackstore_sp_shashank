import { Component, OnInit } from '@angular/core';
import { StockNotificationService } from '../../core/services/stock-notification.service';
import { StockNotificationItem } from '../../models';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html'
})
export class NotificationsComponent implements OnInit {
  loading = true;
  error = '';
  message = '';
  totalCount = 0;
  notifications: StockNotificationItem[] = [];

  constructor(private stockNotificationService: StockNotificationService) {}

  ngOnInit(): void {
    this.stockNotificationService.fetchAllNotifications().subscribe({
      next: (res) => {
        this.loading = false;
        this.message = res.message;
        this.totalCount = res.totalCount;
        this.notifications = res.notifications ?? [];
      },
      error: () => {
        this.loading = false;
        this.error = 'Failed to fetch notifications.';
      }
    });
  }
}
