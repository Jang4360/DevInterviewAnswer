import React from "react";

export default function Spinner() {
  return (
    <div className="flex justify-center h-screen bg-[#212121] text-white">
      <div className="text-center mt-60">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-white mx-auto mb-4"></div>
        <p>인터뷰 질문 생성중입니다...</p>
      </div>
    </div>
  );
}
