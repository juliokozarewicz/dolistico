import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

const config = require('./src/config.json')

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  base: config.BASE_PATH
})