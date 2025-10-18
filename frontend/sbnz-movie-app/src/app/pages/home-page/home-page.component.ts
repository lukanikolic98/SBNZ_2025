import { Component, OnInit } from '@angular/core';
import { Movie } from '../../models/movie.model';
import { MatButton } from '@angular/material/button';
import { MoviesLayoutComponent } from '../../shared/components/movies-layout/movies-layout.component';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.scss',
})
export class HomePageComponent implements OnInit {
  movies: Movie[] = [];

  ngOnInit() {
    this.movies = [
      {
        id: 1,
        title: 'The Shawshank Redemption',
        description: 'A mind-bending thriller by Christopher Nolan.',
        score: 87,
        posterUrl:
          'https://www.themoviedb.org/t/p/w600_and_h900_bestv2/9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg',
      },
      {
        id: 2,
        title: 'Interstellar',
        description: 'A journey through space and time.',
        score: 92,
        posterUrl:
          'https://www.themoviedb.org/t/p/w600_and_h900_bestv2/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg',
      },
      {
        id: 3,
        title: 'The Matrix',
        description: 'Reality is not what it seems.',
        score: 84,
        posterUrl:
          'https://www.themoviedb.org/t/p/w600_and_h900_bestv2/p96dm7sCMn4VYAStA6siNz30G1r.jpg',
      },
    ];
  }
}
