"use client";

import { useState } from "react";
import { usePathname } from "next/navigation";
import { FiMenu } from "react-icons/fi";
import Header from "./Header";
import Sidebar from "./Sidebar";

export default function Layout({ children }: { children: React.ReactNode }) {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const pathname = usePathname();
  const isAuthPage = pathname === "/login" || pathname === "/signup";

  if (isAuthPage) return <>{children}</>;

  return (
    <div className="flex h-screen bg-[#212121] overflow-hidden relative">
      {/* 사이드바 토글 버튼 */}
      <button
        onClick={() => setIsSidebarOpen(!isSidebarOpen)}
        className="cursor-pointer fixed top-4 left-4 z-50 text-white sm:hidden"
      >
        <FiMenu size={24} />
      </button>

      {/* 사이드바 */}
      <Sidebar isSidebarOpen={isSidebarOpen} />

      {/* 오른쪽 컨텐츠 영역 */}
      <div
        className={`flex flex-col transition-all duration-500 ease-in-out ${
          isSidebarOpen ? "sm:ml-60" : "ml-0"
        } w-full`}
      >
        <Header toggleSidebar={() => setIsSidebarOpen(!isSidebarOpen)} />
        <main className="p-4 sm:p-6">{children}</main>
      </div>
    </div>
  );
}
