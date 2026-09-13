import { Component } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { ThemeToggle } from '../../shared/theme-toggle/theme-toggle';
import { ArchitectureDiagram } from './architecture-diagram/architecture-diagram';

@Component({
  selector: 'app-landing',
  imports: [RouterLink, MatButton, MatIcon, ThemeToggle, ArchitectureDiagram],
  templateUrl: './landing.html',
  styleUrl: './landing.scss',
})
export class Landing {}
