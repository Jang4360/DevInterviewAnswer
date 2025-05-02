"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/router";
import Button from "@/components/common/Button";
import api from "@/utils/api";
import useTodayReviews from "@/stores/useTodayReviews";
import useAuthGuard from "@/hooks/useAuthGuard";

export default function QuestionDetailPage() {
  useAuthGuard();
  const router = useRouter();
  const { questionId, fromSidebar } = router.query;
  const [questionDetail, setQuestionDetail] = useState(null);
  const [showAnswer, setShowAnswer] = useState(false);
  const { removeReviewById } = useTodayReviews();

  useEffect(() => {
    if (!router.isReady || !questionId) return;
    fetchQuestionDetail();
  }, [router.isReady, questionId]);

  const fetchQuestionDetail = async () => {
    try {
      const res = await api.get(`/qna/${questionId}`);
      setQuestionDetail(res.data);
    } catch (error) {
      console.error("질문 조회 실패", error);
    }
  };

  const handleCompleteReview = async () => {
    const userId = localStorage.getItem("userId");
    try {
      await api.post(`/review`, {
        userId: userId,
        qnaId: questionDetail.id,
      });
      alert("복습 완료 처리되었습니다!");
      removeReviewById(questionDetail.id);
      router.push("/review");
    } catch (error) {
      console.error(error);
      alert("복습 완료 실패");
    }
  };

  if (!questionDetail) {
    return <div className="p-6 text-white">로딩 중...</div>;
  }

  return (
    <div className="p-6 text-white">
      <h2 className="text-xl font-bold mb-6">{questionDetail.question}</h2>

      <textarea
        className="w-full p-4 rounded bg-[#303030] text-white mb-6"
        rows={6}
        placeholder="답변을 작성해보세요..."
      />

      <div className="flex justify-center">
        <Button
          onClick={() => setShowAnswer(!showAnswer)}
          className="cursor-pointer px-6 py-3"
        >
          {showAnswer ? "답변 숨기기" : "답변 확인하기"}
        </Button>
      </div>

      {showAnswer && (
        <div className="mt-4 p-4 bg-[#404040] rounded">
          <strong>모범 답변:</strong>
          <p className="mt-2">{questionDetail.answer}</p>
        </div>
      )}

      {fromSidebar && (
        <div className="flex justify-center mt-6">
          <Button
            onClick={handleCompleteReview}
            className="cursor-pointer px-6 py-3"
          >
            복습 완료
          </Button>
        </div>
      )}
    </div>
  );
}
