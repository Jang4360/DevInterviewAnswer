"use client";

import Link from "next/link";
import { FiUser } from "react-icons/fi";

export default function Header() {
  return (
    <header className="flex items-center justify-between bg-[#212121] text-white px-6 py-4 border-b border-gray-700 w-full">
      {/* 왼쪽 여백 - 비움 */}
      <div className="w-24"></div>

      {/* 중앙 DevInterview */}
      <div className="flex-1 flex justify-center">
        <Link href="/review">
          <h1 className="text-xl font-bold cursor-pointer">DevInterview</h1>
        </Link>
      </div>

      {/* 오른쪽 - Create 버튼 + User 아이콘 */}
      <div className="flex items-center space-x-4">
        <Link href="/create">
          <button id="create-btn" className="border px-3 py-1 rounded">
            Create
          </button>
        </Link>
        <Link href="/my">
          <FiUser size={20} className="cursor-pointer" />
        </Link>
      </div>
    </header>
  );
}
