import { Component, AfterViewInit } from '@angular/core';

@Component({
  selector: 'app-update-email',
  standalone: true,
  templateUrl: '../update-email.html',
  styleUrl: '../css/update-email.css'
})

export class UpdateEmailComponent implements AfterViewInit {

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