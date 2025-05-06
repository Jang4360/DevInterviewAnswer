"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/router";
import api from "@/utils/api";
import useAuthGuard from "@/hooks/useAuthGuard";

export default function MyPage() {
  useAuthGuard();
  const router = useRouter();
  const [questionCount, setQuestionCount] = useState<number | null>(null);
  const [reviewCount, setReviewCount] = useState<number | null>(null);
  const [latestReviewDate, setLatestReviewDate] = useState<string | null>(null);

  const userId =
    typeof window !== "undefined" ? localStorage.getItem("userId") : null;

  useEffect(() => {
    if (!userId) return;

    const fetchData = async () => {
      try {
        const [qnaRes, reviewRes, latestRes] = await Promise.all([
          api.get(`/qna/user/${userId}/count`),
          api.get(`/review/user/${userId}/count`),
          api.get(`/review/user/${userId}/latest`),
        ]);

        setQuestionCount(qnaRes.data);
        setReviewCount(reviewRes.data.count); // 백엔드에 따라 수정
        setLatestReviewDate(
          latestRes.data
            ? new Date(latestRes.data).toLocaleDateString("ko-KR")
            : "없음"
        );
      } catch (error) {
        console.error("마이페이지 데이터 불러오기 실패", error);
      }
    };

    fetchData();
  }, [userId]);

  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userId");
    router.push("/login");
  };

  return (
    <div className="p-6 text-white flex flex-col items-center mt-10 bg-[#212121] w-full overflow-x-auto pb-20">
      <h2 className="text-3xl font-bold mb-6">마이페이지</h2>

      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 w-full px-4 sm:px-0 max-w-3xl mb-8 mt-10">
        <div className="bg-[#2a2a2a] p-6 rounded-lg shadow-md text-center">
          <h3 className="text-lg font-semibold mb-2">누적 질문 수</h3>
          <p className="text-2xl font-bold">
            {questionCount !== null ? `${questionCount}개` : "로딩 중..."}
          </p>
        </div>
        <div className="bg-[#2a2a2a] p-6 rounded-lg shadow-md text-center">
          <h3 className="text-lg font-semibold mb-2">누적 복습 횟수</h3>
          <p className="text-2xl font-bold">
            {reviewCount !== null ? `${reviewCount}회` : "로딩 중..."}
          </p>
        </div>
        <div className="bg-[#2a2a2a] p-6 rounded-lg shadow-md text-center">
          <h3 className="text-lg font-semibold mb-2">최근 복습일</h3>
          <p className="text-2xl font-bold">
            {latestReviewDate || "로딩 중..."}
          </p>
        </div>
      </div>

      <button
        onClick={handleLogout}
        className="cursor-pointer h-12 w-40 border border-white rounded text-lg hover:bg-[#303030] transition mt-10"
      >
        로그아웃
      </button>
    </div>
  );
}
