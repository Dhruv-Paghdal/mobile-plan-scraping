import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

let targetVar = NGROK_LINK;

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: targetVar, 
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
        secure: false,
      },
      '/suggestion': {
        target: targetVar, 
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/suggestion/, ''),
        secure: false,
      },
      '/spellCheck': {
        target: targetVar,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/spellCheck/, ''),
        secure: false,
      },
      '/pageRanking': {
        target: targetVar,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/pageRanking/, ''),
        secure: false,
      },
      '/searchFreq': {
        target: targetVar,
        changeOrigin: true,
        rewrite: (path)=> path.replace(/^\/searchFreq/, ''),
        secure: false,
      }
    },
  },
});