import { Component, AfterViewInit } from '@angular/core';

@Component({
    selector: 'app-delete-account',
    standalone: true,
    templateUrl: '../delete-account.html',
    styleUrl: '../css/delete-account.css'
})

export class DeleteAccountComponent implements AfterViewInit {

    ngAfterViewInit(): void {
        this.initializePage();
    }

    private initializePage(): void {

        const loading = document.getElementById('loading');
        const formUpdatepasswordFrame = document.getElementById('formUpdatepasswordFrame');

        setTimeout(() => {

            if (!loading || !formUpdatepasswordFrame) { return; }

            loading.style.display = 'none';
            formUpdatepasswordFrame.style.display = 'flex';

        }, 1000 );

    }

}