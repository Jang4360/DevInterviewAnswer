// pages/review/generated.tsx
"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/router";
import Button from "@/components/common/Button";
import api from "@/utils/api";
import useTodayReviews from "@/stores/useTodayReviews";
import useAuthGuard from "@/hooks/useAuthGuard";

type QnaItem = {
  question: string;
  answer: string;
};

export default function GeneratedReviewPage() {
  useAuthGuard();
  const [qnaList, setQnaList] = useState<QnaItem[]>([]);
  const [selectedIndexes, setSelectedIndexes] = useState<number[]>([]);
  const router = useRouter();
  const { fetchReviews } = useTodayReviews();

  useEffect(() => {
    const savedQnA = localStorage.getItem("generatedQnA");
    if (savedQnA) {
      setQnaList(JSON.parse(savedQnA));
    }
  }, []);

  const toggleSelect = (index: number) => {
    if (selectedIndexes.includes(index)) {
      setSelectedIndexes(selectedIndexes.filter((i) => i !== index));
    } else {
      setSelectedIndexes([...selectedIndexes, index]);
    }
  };

  const handleSave = async () => {
    const userId = localStorage.getItem("userId");
    const writingId = localStorage.getItem("writingId");
    if (!userId || !writingId) {
      alert("유저 정보가 없습니다. 다시 로그인 해주세요.");
      return;
    }
    const selectedQnAs = selectedIndexes.map((index) => qnaList[index]);
    try {
      for (const qna of selectedQnAs) {
        await api.post(`/qna/${writingId}`, {
          userId,
          question: qna.question,
          answer: qna.answer,
        });
      }
      await fetchReviews(userId);

      alert("질문 저장 완료!");
      localStorage.removeItem("generatedQnA");
      router.push("/review");
    } catch (error) {
      console.error(error);
      alert("저장 실패");
    }
  };

  return (
    <div className="p-6 text-white">
      <h2 className="text-xl font-bold mb-4">생성된 질문 목록</h2>

      {qnaList.map((qna: QnaItem, index: number) => (
        <div key={index} className="bg-[#303030] p-4 rounded mb-4">
          <label className="flex items-start space-x-2">
            <input
              type="checkbox"
              checked={selectedIndexes.includes(index)}
              onChange={() => toggleSelect(index)}
            />
            <div>
              <p>
                <strong>Q:</strong> {qna.question}
              </p>
              <p>
                <strong>A:</strong> {qna.answer}
              </p>
            </div>
          </label>
        </div>
      ))}

      <div className="flex justify-center mt-6">
        <Button onClick={handleSave} className="cursor-pointer px-6 py-3">
          저장하기
        </Button>
      </div>
    </div>
  );
}
