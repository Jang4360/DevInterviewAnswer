"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import Spinner from "@/components/common/Spinner";
import api from "@/utils/api";
import useAuthGuard from "@/hooks/useAuthGuard";

export default function SpinnerPage() {
  useAuthGuard();
  const router = useRouter();

  useEffect(() => {
    console.log("generateQNA 실행됨");
    const generateQnA = async () => {
      const writingId = localStorage.getItem("writingId");
      const userId = localStorage.getItem("userId");

      try {
        // 글 내용 조회
        const writingRes = await api.get(`/writings/${writingId}`);
        const content = writingRes.data.content;

        // GPT 질문 생성 API 호출
        const res = await api.post("/ai/generate-questions", {
          userId,
          content,
        });

        const qnaList = res.data.qnaList;
        localStorage.setItem("generatedQnA", JSON.stringify(qnaList));

        // 💡 질문 상세 페이지로 이동
        router.push("/review/generated");
      } catch (error) {
        console.error("질문 생성 실패:", error);
        alert("질문 생성 실패");
        router.push("/review");
      }
    };

    // 💡 API 호출은 스피너 페이지에서 수행
    generateQnA();
  }, [router]);

  return <Spinner />;
}
