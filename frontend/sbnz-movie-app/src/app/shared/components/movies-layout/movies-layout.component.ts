import { Component, Input } from '@angular/core';
import { Movie } from '../../../models/movie.model';
import { MovieCardComponent } from '../movie-card/movie-card.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-movies-layout',
  standalone: true,
  imports: [MovieCardComponent, CommonModule],
  templateUrl: './movies-layout.component.html',
  styleUrl: './movies-layout.component.scss',
})
export class MoviesLayoutComponent {
  @Input() movies!: Movie[];

  ngOnChanges() {
    console.log('MoviesLayout movies input:', this.movies);
  }
}
