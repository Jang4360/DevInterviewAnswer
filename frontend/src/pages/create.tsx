"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import Button from "@/components/common/Button";
import api from "../utils/api";
import useAuthGuard from "@/hooks/useAuthGuard";

export default function CreatePage() {
  useAuthGuard();
  const [content, setContent] = useState("");
  const router = useRouter();

  const handleGenerateQnA = async () => {
    try {
      const userId = localStorage.getItem("userId");
      if (!userId) {
        alert("로그인이 필요합니다.");
        router.push("/login");
        return;
      }

      // 글 저장
      const writingRes = await api.post("/writings", {
        userId,
        content,
      });

      const { id: writingId } = writingRes.data;
      localStorage.setItem("writingId", writingId);
      localStorage.setItem("writingContent", content);

      // 인터뷰 질문 생성 API 호출
      try {
        await api.post("/ai/generate-questions", {
          userId,
          content,
        });

        // Spinner 페이지로 이동해서 GPT 생성 대기
        router.push("/spinner");
      } catch (err: any) {
        // 중복 요청 시 에러 처리
        if (err.response?.status === 429) {
          alert(
            err.response.data.message ||
              "중복 요청입니다. 잠시 후 다시 시도해 주세요"
          );
        } else {
          alert("인터뷰 질문 생성에 실패했습니다.");
        }
        console.error("질문 생성 에러:", err);
      }
    } catch (error) {
      console.error("글 저장 실패:", error);
      alert("글 저장 실패");
    }
  };

  return (
    <div className="p-6 text-white">
      <h2 className="text-xl font-bold mb-4">글 작성</h2>
      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder="내용을 입력하세요"
        className="w-full h-80 p-4 bg-[#303030] text-white rounded mb-4 placeholder-[#BBB5B5]"
      />
      <div className="flex justify-center">
        <Button
          onClick={handleGenerateQnA}
          className="cursor-pointer px-6 py-3"
        >
          인터뷰 질문 생성하기
        </Button>
      </div>
    </div>
  );
}
