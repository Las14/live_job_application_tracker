import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { Application } from '../../../core/models/application.model';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-application-details-dialog',
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule],
  templateUrl: './application-details-dialog.html',
  styleUrl: './application-details-dialog.scss',
})
export class ApplicationDetailsDialog {

  data = inject<Application>(MAT_DIALOG_DATA);
  private dialogRef = inject(MatDialogRef<ApplicationDetailsDialog>);

  close(): void{
    this.dialogRef.close();
  }
}
