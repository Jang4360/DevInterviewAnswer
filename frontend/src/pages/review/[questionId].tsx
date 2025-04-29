"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/router";
import Button from "@/components/common/Button";
import api from "@/utils/api";

export default function QuestionDetailPage() {
  const router = useRouter();
  const { questionId, fromSidebar } = router.query;
  const [questionDetail, setQuestionDetail] = useState(null);
  const [showAnswer, setShowAnswer] = useState(false);

  useEffect(() => {
    if (router.isReady && questionId) {
      fetchQuestionDetail();
    }
  }, [router.isReady, questionId]);

  const fetchQuestionDetail = async () => {
    try {
      const res = await api.get(`/qna/${questionId}`);
      setQuestionDetail(res.data);
    } catch (error) {
      console.error(error);
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

      {!showAnswer ? (
        <div className="flex justify-center">
          <Button onClick={() => setShowAnswer(true)} className="px-6 py-3">
            답변 확인하기
          </Button>
        </div>
      ) : (
        <div className="mt-4 p-4 bg-[#404040] rounded">
          <strong>모범 답변:</strong>
          <p className="mt-2">{questionDetail.answer}</p>
        </div>
      )}

      {fromSidebar && (
        <div className="flex justify-center mt-6">
          <Button onClick={handleCompleteReview} className="px-6 py-3">
            복습 완료
          </Button>
        </div>
      )}
    </div>
  );
}
