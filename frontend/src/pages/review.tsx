"use client";

import React, { useState, useEffect } from "react";
import Input from "@/components/common/Input";
import Modal from "@/components/common/Modal";
import { FiTrash2 } from "react-icons/fi";
import Link from "next/link";
import api from "../utils/api";
import useTodayReviews from "@/stores/useTodayReviews";
import useAuthGuard from "@/hooks/useAuthGuard";

export default function ReviewPage() {
  useAuthGuard();
  const [searchTerm, setSearchTerm] = useState("");
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0); // ✅ 페이지 번호 상태 추가
  const [loading, setLoading] = useState(false); // ✅ 로딩 상태 추가
  const [hasMore, setHasMore] = useState(true); // ✅ 더 불러올 데이터 여부
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const { removeReviewById } = useTodayReviews();

  // ✅ 데이터 페이징 요청 함수
  const fetchQnAs = async (page = 0, size = 10) => {
    const userId = localStorage.getItem("userId");
    try {
      setLoading(true);
      const res = await api.get(`/qna/user/${userId}`, {
        params: { page, size },
      });
      console.log("서버에서 받은 데이터:", res.data);

      // ✅ 데이터 추가 로직
      if (res.data.content.length > 0) {
        setData((prev) => [...prev, ...res.data.content]);
        setPage((prev) => prev + 1);
      } else {
        setHasMore(false); // 더 이상 가져올 데이터 없음
      }
    } catch (error) {
      console.error(error);
      alert("QnA 목록 불러오기 실패");
    } finally {
      setLoading(false);
    }
  };

  // ✅ 스크롤 감지 함수
  const handleScroll = () => {
    if (
      window.innerHeight + document.documentElement.scrollTop >=
        document.documentElement.offsetHeight - 50 &&
      !loading &&
      hasMore
    ) {
      fetchQnAs(page); // ✅ 다음 페이지 가져오기
    }
  };

  useEffect(() => {
    fetchQnAs(); // ✅ 첫 페이지 로드
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  const filteredData = data.filter((item: any) =>
    item.question.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const openModal = (id: string) => {
    setSelectedId(id);
    setModalOpen(true);
  };

  const confirmDelete = async () => {
    const userId = localStorage.getItem("userId");
    try {
      await api.delete(`/qna/${selectedId}?userId=${userId}`);
      setData(data.filter((item: any) => item.id !== selectedId));
      if (selectedId) {
        removeReviewById(selectedId);
      }
      setModalOpen(false);
      setSelectedId(null);
    } catch (error) {
      console.error(error);
      alert("삭제 실패");
    }
  };

  return (
    <div className="p-6">
      {/* 검색창 */}
      <div className="mb-6">
        <Input
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          placeholder="키워드로 검색 하세요"
          className="w-full bg-[#303030]"
        />
      </div>

      {/* QnA 리스트 */}
      <div className="bg-[#303030] rounded-lg p-4 ">
        <table className="w-full text-left text-white">
          <thead>
            <tr className="border-b border-gray-600">
              <th className="p-2">면접 질문</th>
              <th className="p-2">예정 리뷰일</th>
              <th className="p-0">삭제</th>
            </tr>
          </thead>
          <tbody>
            {filteredData.map((item: any) => (
              <tr key={item.id} className="border-b border-gray-700">
                <td className="p-2">
                  <Link href={`/review/${item.id}`}>
                    <p className="text-white hover:underline">
                      {item.question}
                    </p>
                  </Link>
                </td>
                <td className="p-2">
                  {item.scheduleDate ? item.scheduleDate.split("T")[0] : "없음"}
                </td>
                <td className="p-4">
                  <button onClick={() => openModal(item.id)}>
                    <FiTrash2
                      size={16}
                      className="cursor-pointer text-white hover:text-red-500"
                    />
                  </button>
                </td>
              </tr>
            ))}
            {filteredData.length === 0 && (
              <tr>
                <td colSpan={3} className="p-4 text-center text-gray-400">
                  검색 결과가 없습니다.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* 삭제 확인 모달 */}
      <Modal
        isOpen={modalOpen}
        onConfirm={confirmDelete}
        onCancel={() => setModalOpen(false)}
        message="삭제하시겠습니까?"
      />
    </div>
  );
}
