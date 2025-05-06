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
      <div>
        <div className="flex items-center justify-between px-10 py-0">
          <h2 className="text-white text-lg font-bold">Today Review</h2>
        </div>
        <div className="mt-6">
          <ul className="space-y-3">
            {reviews.length > 0 ? (
              reviews.map((review: any) => (
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
      </div>
      <div className="text-sm text-white opacity-50 mt-6">ⓒ DevInterview</div>
    </div>
  );
}
