import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { MatIcon, MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-sidebar',
  imports: [MatIcon,RouterLink, RouterLinkActive, MatIconModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss',
})
export class Sidebar implements OnInit {
  authService = inject(AuthService);
  private router = inject(Router);

  isOpen= signal(false);

  ngOnInit():void{
    this.authService.loadCurrentUser();
  }

  toggle():void{
    this.isOpen.update(v => !v);
  }
  
  close():void{
    this.isOpen.set(false);
  }


  onLogout():void{
    this.authService.logout();
    this.router.navigate(['/login'], {replaceUrl: true});
  }

  profile():void{
    this.authService.getCurrentUser;
  }

  initials(): string{
    const user = this.authService.currentUser();
    if(!user) return '';
    return (user.firstName?.[0] ?? '') + (user.lastName?.[0] ?? '')
  }
}
