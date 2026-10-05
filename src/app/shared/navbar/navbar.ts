import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [ CommonModule,
    RouterLink,
    RouterLinkActive,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  authService = inject(AuthService);
  private router = inject(Router);

  onLogout():void{
    this.authService.logout();
    this.router.navigate(['/login'], {replaceUrl: true});
  }
  register():void{
    this.authService.register;
    
  }
  profile():void{
    this.authService.getCurrentUser;
  }

}
