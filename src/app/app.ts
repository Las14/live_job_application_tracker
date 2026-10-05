import { Component, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { Sidebar } from './shared/sidebar/sidebar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Sidebar],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('frontend');
  private router = inject(Router);
  showSidebar = signal(!this.isAuthPage(this.router.url));

  constructor(){
    this.router.events.subscribe((event) => {
      if(event instanceof NavigationEnd){
        this.showSidebar.set(!this.isAuthPage(event.urlAfterRedirects));
      }
    });
  }

  private isAuthPage(url: string): boolean{
    return url.startsWith('/login') || url.startsWith('/register');
  }
}
