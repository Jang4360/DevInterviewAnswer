"use client";

import { useState } from "react";
import { usePathname } from "next/navigation";
import { FiMenu } from "react-icons/fi";
import Header from "./Header";
import Sidebar from "./Sidebar";

export default function Layout({ children }: { children: React.ReactNode }) {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const pathname = usePathname();
  const isAuthPage = pathname === "/login" || pathname === "/signup";

  // 🔥 로그인/회원가입 페이지는 레이아웃 제외
  if (isAuthPage) {
    return <>{children}</>;
  }

  return (
    <div className="flex h-screen bg-[#212121]">
      {/* 🔥 왼쪽 상단 고정 버튼 */}
      <button
        onClick={() => setIsSidebarOpen(!isSidebarOpen)}
        className="fixed top-4 left-4 z-50 text-white"
      >
        <FiMenu size={24} />
      </button>

      {/* 🔥 사이드바 */}
      {isSidebarOpen && (
        <Sidebar
          isSidebarOpen={isSidebarOpen}
          toggleSidebar={() => setIsSidebarOpen(!isSidebarOpen)}
        />
      )}

      {/* 🔥 오른쪽 영역 */}
      <div className="flex flex-col flex-1 transition-all duration-300">
        <Header toggleSidebar={() => setIsSidebarOpen(!isSidebarOpen)} />
        <main className="p-6">{children}</main>
      </div>
    </div>
  );
}
