"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import Spinner from "@/components/common/Spinner";
import api from "@/utils/api";

export default function SpinnerPage() {
  const router = useRouter();

  useEffect(() => {
    console.log("generateQNA 실행됨");
    const generateQnA = async () => {
      const writingId = localStorage.getItem("writingId");
      const userId = localStorage.getItem("userId");

      const writingRes = await api.get(`/writings/${writingId}`);
      const content = writingRes.data.content;

      try {
        // GPT 질문 생성 API 호출
        const res = await api.post("/ai/generate-questions", {
          userId,
          content, // 필요 시 content 전달
        });

        const qnaList = res.data.qnaList;

        // 생성된 질문 리스트 저장
        localStorage.setItem("generatedQnA", JSON.stringify(qnaList));

        // 질문 상세 페이지로 이동
        router.push("/review/generated?fromCreate=true");
      } catch (error) {
        console.error(error);
        alert("질문 생성 실패");
        router.push("/review");
      }
    };

    generateQnA();
  }, [router]);

  return <Spinner />;
}
