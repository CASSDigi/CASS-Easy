import React, { useState } from 'react';
import { 
  Terminal, 
  Settings, 
  Code2, 
  Rocket, 
  ExternalLink, 
  Copy, 
  Check, 
  Server, 
  Layers,
  ChevronRight,
  Sparkles
} from 'lucide-react';

export default function App() {
  const [copied, setCopied] = useState<string | null>(null);
  const [selectedTab, setSelectedTab] = useState<'vite' | 'next' | 'vanilla'>('vite');

  const copyToClipboard = (text: string, id: string) => {
    navigator.clipboard.writeText(text);
    setCopied(id);
    setTimeout(() => setCopied(null), 2000);
  };

  return (
    <div className="min-h-screen bg-[#0A0A0A] text-[#E0E0E0] font-sans flex flex-col selection:bg-[#F38020] selection:text-black">
      {/* Top Navigation */}
      <header className="border-b border-[#222] bg-[#111]/80 backdrop-blur-md px-6 py-4 flex items-center justify-between sticky top-0 z-50">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-[#F38020] to-[#FAAD3F] flex items-center justify-center shadow-lg shadow-[#F38020]/20">
            <Rocket className="w-4 h-4 text-black font-bold" />
          </div>
          <div>
            <h1 className="text-base font-semibold text-white tracking-tight">Cloudflare Pages App</h1>
            <p className="text-xs text-[#888]">Ready for production deployment</p>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <a 
            href="https://developers.cloudflare.com/pages/" 
            target="_blank" 
            rel="noreferrer"
            className="flex items-center gap-1.5 text-xs text-[#AAA] hover:text-white px-3 py-1.5 rounded-md hover:bg-[#1A1A1A] transition-colors"
          >
            <span>Cloudflare Docs</span>
            <ExternalLink className="w-3 h-3" />
          </a>
        </div>
      </header>

      {/* Hero / Overview */}
      <main className="flex-1 max-w-5xl w-full mx-auto p-6 md:p-10 flex flex-col gap-8">
        <div className="bg-[#141414] border border-[#262626] rounded-2xl p-6 md:p-8 relative overflow-hidden">
          <div className="absolute top-0 right-0 w-64 h-64 bg-[#F38020]/10 rounded-full blur-3xl pointer-events-none" />
          <div className="flex items-center gap-2 text-[#F38020] text-xs font-semibold tracking-wider uppercase mb-2">
            <Sparkles className="w-3.5 h-3.5" />
            <span>Cloudflare Edge Ready</span>
          </div>
          <h2 className="text-2xl md:text-3xl font-bold text-white mb-3">Your React Application is Configured</h2>
          <p className="text-sm md:text-base text-[#999] max-w-2xl leading-relaxed">
            This application is built with Vite, React 19, and Tailwind CSS. It is configured to build seamlessly on Cloudflare Pages or direct edge deployment.
          </p>

          <div className="mt-6 flex flex-wrap items-center gap-3">
            <div className="flex items-center gap-2 px-3 py-1.5 bg-[#1C1C1C] border border-[#333] rounded-lg text-xs font-mono text-[#DDD]">
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
              Build Command: <span className="text-[#FAAD3F]">npm run build</span>
            </div>
            <div className="flex items-center gap-2 px-3 py-1.5 bg-[#1C1C1C] border border-[#333] rounded-lg text-xs font-mono text-[#DDD]">
              <span className="w-2 h-2 rounded-full bg-blue-500" />
              Output Directory: <span className="text-[#FAAD3F]">dist</span>
            </div>
          </div>
        </div>

        {/* Quick CLI Deployment Guide */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="bg-[#141414] border border-[#262626] rounded-xl p-6 flex flex-col justify-between">
            <div>
              <div className="flex items-center gap-2.5 text-white font-semibold text-sm mb-2">
                <Terminal className="w-4 h-4 text-[#F38020]" />
                <h3>Direct Edge Deploy (Wrangler)</h3>
              </div>
              <p className="text-xs text-[#888] mb-4 leading-relaxed">
                Deploy in seconds directly from your machine without waiting for GitHub Actions:
              </p>
              <div className="bg-[#0D0D0D] border border-[#222] rounded-lg p-3 font-mono text-xs text-[#CCC] flex items-center justify-between group">
                <span className="select-all">npx wrangler pages deploy dist</span>
                <button
                  onClick={() => copyToClipboard('npx wrangler pages deploy dist', 'wrangler')}
                  className="text-[#666] hover:text-white p-1 rounded transition"
                  title="Copy command"
                >
                  {copied === 'wrangler' ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                </button>
              </div>
            </div>
          </div>

          <div className="bg-[#141414] border border-[#262626] rounded-xl p-6 flex flex-col justify-between">
            <div>
              <div className="flex items-center gap-2.5 text-white font-semibold text-sm mb-2">
                <Server className="w-4 h-4 text-[#F38020]" />
                <h3>Local Development</h3>
              </div>
              <p className="text-xs text-[#888] mb-4 leading-relaxed">
                Start your local dev server with lightning fast HMR:
              </p>
              <div className="bg-[#0D0D0D] border border-[#222] rounded-lg p-3 font-mono text-xs text-[#CCC] flex items-center justify-between group">
                <span className="select-all">npm run dev</span>
                <button
                  onClick={() => copyToClipboard('npm run dev', 'dev')}
                  className="text-[#666] hover:text-white p-1 rounded transition"
                  title="Copy command"
                >
                  {copied === 'dev' ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                </button>
              </div>
            </div>
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t border-[#1C1C1C] py-4 px-6 text-center text-xs text-[#555]">
        Powered by Vite + React + Cloudflare Pages Edge Runtime
      </footer>
    </div>
  );
}
