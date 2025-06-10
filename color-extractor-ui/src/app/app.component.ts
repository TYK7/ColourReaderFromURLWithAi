import { Component } from '@angular/core';

import { ColorInputDisplayComponent } from './color-input-display/color-input-display.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ColorInputDisplayComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'color-extractor-ui';
}
