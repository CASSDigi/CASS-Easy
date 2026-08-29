export type PackageManager = 'npm' | 'pnpm' | 'yarn' | 'bun';

export type FrameworkId = 
  | 'vite-react'
  | 'nextjs-export'
  | 'nextjs-edge'
  | 'vue-vite'
  | 'astro'
  | 'sveltekit'
  | 'nuxt3'
  | 'remix-cloudflare'
  | 'solidjs'
  | 'vanilla-html'
  | 'custom';

export interface FrameworkPreset {
  id: FrameworkId;
  name: string;
  category: 'SPA' | 'SSG' | 'SSR / Hybrid' | 'Static';
  cloudflarePresetName: string;
  iconName: string;
  defaultBuildCommand: Record<PackageManager, string>;
  defaultOutputDir: string;
  nodeVersion: '18' | '20' | '22';
  needsRedirectsForSpa: boolean;
  defaultRedirectsContent?: string;
  description: string;
  proTip: string;
  commonCommands: {
    title: string;
    stage: 'DEV' | 'BUILD' | 'PROD' | 'DEPLOY';
    command: string;
  }[];
  edgeRuntimeNote?: string;
  recommendedEnvVars?: { key: string; value: string; desc: string }[];
}

export interface BuildConfiguration {
  frameworkId: FrameworkId;
  packageManager: PackageManager;
  buildCommand: string;
  outputDirectory: string;
  rootDirectory: string;
  nodeVersion: '18' | '20' | '22';
  projectName: string;
  includeSpaRedirect: boolean;
  customEnvVars: { id: string; key: string; value: string; isSecret: boolean; environment: 'all' | 'production' | 'preview' }[];
}

export type ActiveTab = 'build' | 'functions' | 'env' | 'redirects' | 'cli';
