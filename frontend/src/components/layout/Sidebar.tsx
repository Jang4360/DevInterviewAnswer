"use client";

import React, { useEffect } from "react";
import Link from "next/link";
import useTodayReviews from "@/stores/useTodayReviews";

interface SidebarProps {
  isSidebarOpen: boolean;
}

export default function Sidebar({ isSidebarOpen }: SidebarProps) {
  const { reviews, fetchReviews } = useTodayReviews();

  useEffect(() => {
    const userId = localStorage.getItem("userId");
    if (userId) fetchReviews(userId);
  }, [fetchReviews]);

  return (
    <div
      className={`fixed top-0 left-0 h-full w-60 bg-[#171717] text-white flex flex-col justify-between p-4 z-40 
        transform transition-transform duration-500 ease-in-out ${
          isSidebarOpen ? "translate-x-0" : "-translate-x-full"
        }`}
    >
      <div className="flex items-center justify-between px-10 py-0">
        <h2 className="text-white text-lg font-bold">Today Review</h2>
      </div>
      {/* 질문 목록: 사이드바 높이 가득 채우기*/}
      <div className="flex-1 mt-2 overflow-y-auto">
        <ul className="space-y-3 px-2">
          {reviews.length > 0 ? (
            reviews
              .filter((review) => !review.isDeleted)
              .map((review) => (
                <li
                  key={review.id}
                  className="text-sm hover:underline cursor-pointer"
                >
                  <Link
                    href={{
                      pathname: `/review/${review.id}`,
                      query: { fromSidebar: true },
                    }}
                  >
                    {review.question}
                  </Link>
                </li>
              ))
          ) : (
            <li className="text-sm text-gray-400">
              오늘 복습할 질문이 없습니다.
            </li>
          )}
        </ul>
      </div>
      <div className="text-sm text-white opacity-50 mt-4">ⓒ DevInterview</div>
    </div>
  );
}
