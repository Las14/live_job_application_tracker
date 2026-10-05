import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ApplicationService } from '../../../core/services/application.service';
import { Application } from '../../../core/models/application.model';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../core/services/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { ApplicationDetailsDialog } from '../application-details-dialog/application-details-dialog';

type StatusFilter = 'ALL' | 'APPLIED' | 'INTERVIEW' | 'OFFER' | 'REJECTED';
type SortOrder = 'newest' | 'oldest';
type ViewMode = 'grid' | 'list';
@Component({
  selector: 'app-application-list',
  imports: [CommonModule, RouterLink, MatIconModule, MatCardModule, MatButtonModule, MatProgressSpinnerModule],
  templateUrl: './application-list.html',
  styleUrl: './application-list.scss',
})

export class ApplicationList  implements OnInit{

  private appllicationService = inject(ApplicationService);
  public authService = inject(AuthService);

  applications= signal<Application[]>([]);
  isLoading = signal(true);
  errorMessage = signal('');
  activeFilter = signal<StatusFilter>('ALL');
  sortOrder = signal<SortOrder>('newest');
  viewMode = signal<ViewMode>('grid');

  totalCount = computed(() => this.applications().length);
  appliedCount = computed(() => this.applications().filter(a=> a.status === 'APPLIED').length);
  inProgressCount = computed(() => this.applications().filter(a=> a.status === 'INTERVIEW').length);
  rejectedCount = computed(() => this.applications().filter(a=> a.status === 'REJECTED').length);
  interviewCount = computed(() => this.applications().filter(a=> a.status === 'INTERVIEW').length);
  offerCount = computed(() => this.applications().filter(a=> a.status === 'OFFER').length);

  filteredApplications = computed(() =>{
    let list = this.applications();

    const filter = this.activeFilter();
    if(filter !== 'ALL'){
      list = list.filter(a=> a.status === filter);
    }

    const order = this.sortOrder();
    list = [...list].sort((a,b) =>{
      const dateA = new Date(a.dateApplied).getTime();
      const dateB = new Date(b.dateApplied).getTime();
      return order === 'newest' ? dateB - dateA : dateA - dateB;
    });
    return list;
  });
  
  setFilter(filter: StatusFilter): void{
    this.activeFilter.set(filter);
  }
  toggleSort():void{
    this.sortOrder.update(order => order === 'newest' ? 'oldest' : 'newest');
  }

  setViewMode(mode: ViewMode): void{
    this.viewMode.set(mode);
  }
  
  
  private dialog = inject(MatDialog);
  
  ngOnInit(): void {
    this.loadApplications();
  }

  loadApplications():void{
    this.isLoading.set(true);
    this.errorMessage.set('');
    this.appllicationService.getAll().subscribe({
      next: (applications) =>{
        this.applications.set(applications);
        this.isLoading.set(false);
      },

      error: (error) => {
        console.log('Faild to load applications', error);
        if(error.error && typeof error.error === 'object'){
          const firstMessage = Object.values(error.error)[0];
          this.errorMessage.set((firstMessage as string) || 'Could not load your applications. Please try agin.')
        }else{
           this.errorMessage.set('Could not load your application. Please try again');
        }
       this.isLoading.set(false);
      }
    });
  }

  onDelete(id:number):void{
    if(!confirm ('Are you sure you want to delete this application?')){
      return;
    }
    this.appllicationService.delete(id).subscribe({
      next:() => {
        this.applications.update(list => list.filter(app => app.id !==id));
      }, 
      error:(error) => {
        console.error('Failed to delete application', error);
        alert('Coulde not delete this application. Please try agian.')
      }
    });
  }

  private avatarColors = ['#4285F4', '#34A853', '#EA4335', '#FBBC05', '#9C27B0', '#00ACC1', '#FF7043'];
  companyInitial(company : string): string{
    return company?.charAt(0).toUpperCase()?? '?';
  }

  companyColor(company: string) : string{
    if(!company) return this.avatarColors[0];
    const index = company.charCodeAt(0) % this.avatarColors.length;
    return this.avatarColors[index];
  }

  statusClass(status:string): string{
    switch(status){
      case 'APPLIED': return 'status-applied';
      case 'INTERVIEW': return 'status-interview';
      case 'OFFER': return 'status-offer';
      case 'REJECTED': return 'status-rejected';
      default: return '';
    }
  }

  statusLabel(status: string) :string{
      switch(status){
      case 'APPLIED': return 'Applied';
      case 'INTERVIEW': return 'Interview';
      case 'OFFER': return 'Offer';
      case 'REJECTED': return 'Rejected';
      default: return status;
    }
  }

  viewDetails(application: Application): void{
    this.dialog.open(ApplicationDetailsDialog,{
      data: application,
    });
  }
}