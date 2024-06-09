/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Roboto', 'Helvetica', 'Arial', 'sans-serif'],
        title: ['Bebas Neue', 'cursive'],
        anton: ['Anton', 'sans-serif'],
        oswald: ['Oswald', 'sans-serif'],
        poppins: ['Poppins', 'sans-serif'],
        proxima: ['Proxima Nova', 'sans-serif'],
        futura: ['Futura PT', 'sans-serif'],
        avenir: ['Avenir', 'sans-serif'],
        helvetica: ['Helvetica Neue', 'sans-serif'],
        spartan: ['League Spartan', 'sans-serif'],
        raleway: ['Raleway', 'sans-serif'],
        montserrat: ['Montserrat', 'sans-serif'],
        pressstart: ['Press Start 2P', 'cursive'],
        exo: ['Exo', 'sans-serif'],
        lobster: ['Lobster', 'cursive'],
        schibsted: ['Schibsted Grotesk', 'sans-serif'], // Añadir esta línea
      },
    },
  },
  plugins: [],
}