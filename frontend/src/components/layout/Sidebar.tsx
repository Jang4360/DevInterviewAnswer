"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import api from "@/utils/api";

export default function Sidebar() {
  const [todayReviews, setTodayReviews] = useState([]);

  useEffect(() => {
    const fetchTodayReviews = async () => {
      const userId = localStorage.getItem("userId");
      try {
        const res = await api.get(`/qna/today?userId=${userId}`);
        setTodayReviews(res.data);
      } catch (error) {
        console.error(error);
      }
    };

    fetchTodayReviews();
  }, []);

  return (
    <div className="w-60 bg-[#171717] text-white flex flex-col justify-between h-full p-4">
      <div>
        {/* 상단 영역 */}
        <div className="flex items-center justify-between px-10 py-0">
          <h2 className="text-white text-lg font-bold">Today Review</h2>
        </div>

        {/* 🔥 리스트와 상단 사이 여백 추가 */}
        <div className="mt-4">
          <ul className="space-y-3">
            {todayReviews.length > 0 ? (
              todayReviews.map((review: any) => (
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
