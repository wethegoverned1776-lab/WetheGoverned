import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { Web3Provider } from "@/components/providers/Web3Provider";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "We The People Platform",
  description: "Censorship-resistant civic engagement for U.S. Congressional Districts",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <Web3Provider>
          <header className="p-6 border-b border-slate-700 flex justify-between items-center bg-slate-900/50 backdrop-blur-md sticky top-0 z-50">
            <h1 className="text-xl font-bold tracking-tight text-accent">We The People</h1>
            <nav className="flex gap-6 items-center">
              <a href="/polls" className="text-sm font-medium hover:text-accent transition-colors">Polls</a>
              <a href="/scorecards" className="text-sm font-medium hover:text-accent transition-colors">Scorecards</a>
              <div id="wallet-button" className="bg-accent text-slate-900 px-4 py-2 rounded-full text-sm font-bold cursor-pointer hover:bg-white transition-all">
                Connect Wallet
              </div>
            </nav>
          </header>
          <main className="max-w-7xl mx-auto px-6 py-12">
            {children}
          </main>
        </Web3Provider>
      </body>
    </html>
  );
}
