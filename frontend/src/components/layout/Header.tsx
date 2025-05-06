"use client";

import { FC } from "react";
import Link from "next/link";
import { FiUser } from "react-icons/fi";

interface HeaderProps {
  toggleSidebar: () => void;
}

const Header: FC<HeaderProps> = ({ toggleSidebar }) => {
  return (
    <header className="flex items-center justify-between bg-surface text-white px-4 sm:px-6 py-4 border-b border-gray-700 w-full">
      {/* 왼쪽 여백 */}
      <div className="w-16 sm:w-24">
        <button onClick={toggleSidebar} className="text-lg sm:text-xl">
          ☰
        </button>
      </div>

      {/* 중앙 Dev Interview */}
      <div className="flex-1 flex justify-center">
        <Link href="/review">
          <h1 className="text-lg sm:text-xl font-bold cursor-pointer hover:text-[#B3B3B3]">
            Dev Interview
          </h1>
        </Link>
      </div>

      {/* 오른쪽 버튼 */}
      <div className="flex items-center gap-3 sm:gap-6">
        <Link href="/create">
          <button
            id="create-btn"
            className="border px-2 sm:px-3 py-1 rounded text-sm sm:text-lg hover:bg-[#303030] transition"
          >
            Create
          </button>
        </Link>
        <Link href="/my">
          <FiUser
            size={20}
            className="cursor-pointer text-white hover:text-[#B3B3B3]"
          />
        </Link>
      </div>
    </header>
  );
};

export default Header;
