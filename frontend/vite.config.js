import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  /*
  server: {
    host: true, // allow external connections
    port: 5173,
    strictPort: false,
    allowedHosts: ['3d784b7d992f.ngrok-free.app'], // just the hostname
    hmr: {
      host: '3d784b7d992f.ngrok-free.app', // hostname only
      protocol: 'wss', // ngrok serves over https, so HMR must use wss
    },
    
  }, */ 
})
