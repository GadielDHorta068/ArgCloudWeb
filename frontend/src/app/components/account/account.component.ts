import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit, OnDestroy {
  activeSection = 'details';
  currentUser: User | null = null;
  private userSubscription: Subscription | undefined;

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  showSection(section: string, event: Event): void {
    event.preventDefault();
    this.activeSection = section;

    // Lógica para la clase 'active' en los enlaces de navegación
    const links = document.querySelectorAll('.list-group-item');
    links.forEach(link => link.classList.remove('active'));
    (event.currentTarget as HTMLElement).classList.add('active');
  }
}
