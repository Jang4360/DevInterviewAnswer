"use client";
import { FC } from "react";
import Link from "next/link";
import { FiUser } from "react-icons/fi";
interface HeaderProps {
  toggleSidebar: () => void;
}

const Header: FC<HeaderProps> = ({ toggleSidebar }) => {
  return (
    <header className="flex items-center justify-between bg-surface text-white px-6 py-4 border-b border-gray-700 w-full">
      {/* 중앙 DevInterview */}
      <div className="flex-1 flex justify-center">
        <Link href="/review">
          <h1 className="text-xl font-bold cursor-pointer ml-8 sm:ml-32 hover:text-[#B3B3B3]">
            Dev Interview
          </h1>
        </Link>
      </div>

      {/* 오른쪽 - Create 버튼 + User 아이콘 */}
      <div className="flex items-center ">
        <Link href="/create">
          <button
            id="create-btn"
            className="cursor-pointer border px-3 py-1 mr-10 rounded text-lg hover:bg-[#303030] transition"
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
