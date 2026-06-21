import { Component, AfterViewInit } from '@angular/core';

@Component({
  selector: 'app-update-password',
  standalone: true,
  templateUrl: '../update-password.html',
  styleUrl: '../css/update-password.css'
})

export class UpdatePasswordComponent implements AfterViewInit {

  ngAfterViewInit(): void {
    this.initializePage();
  }

  private initializePage(): void {

    const loading = document.getElementById('loading');
    const formUpdatepasswordFrame = document.getElementById('formUpdatepasswordFrame');

    setTimeout(() => {
      if (!loading || !formUpdatepasswordFrame) {
        return;
      }

      loading.style.display = 'none';
      formUpdatepasswordFrame.style.display = 'flex';
    }, 1000);
    
  }

}