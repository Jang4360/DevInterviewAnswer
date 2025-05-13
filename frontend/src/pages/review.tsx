"use client";

import React, { useState, useEffect } from "react";
import Input from "@/components/common/Input";
import Modal from "@/components/common/Modal";
import { FiTrash2 } from "react-icons/fi";
import Link from "next/link";
import api from "../utils/api";
import useTodayReviews from "@/stores/useTodayReviews";
import useAuthGuard from "@/hooks/useAuthGuard";

interface Qna {
  id: string;
  question: string;
  scheduleDate: string;
  reviewCount: number; // ✅ 복습 횟수 필드 추가
}

export default function ReviewPage() {
  useAuthGuard();
  const [searchTerm, setSearchTerm] = useState("");
  const [data, setData] = useState<Qna[]>([]);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
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

      // ✅ 복습 횟수 추가 요청
      const updatedData = await Promise.all(
        res.data.content.map(async (item: Qna) => {
          try {
            const reviewRes = await api.get(`/review/qna/${item.id}/count`);
            item.reviewCount = reviewRes.data.count;
          } catch (error) {
            console.error("복습 횟수 조회 실패:", error);
            item.reviewCount = 0;
          }
          return item;
        })
      );

      // ✅ 데이터 추가 로직
      if (updatedData.length > 0) {
        setData((prev) => [...prev, ...updatedData]);
        setPage((prev) => prev + 1);
      } else {
        setHasMore(false);
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
      fetchQnAs(page);
    }
  };

  useEffect(() => {
    fetchQnAs();
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  const filteredData = data.filter((item: Qna) =>
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
      setData(data.filter((item: Qna) => item.id !== selectedId));
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
      <div className="bg-[#303030] rounded-lg p-4 max-h-[600px] overflow-y-auto">
        <table className="w-full text-left text-white">
          <thead>
            <tr className="border-b border-gray-600">
              <th className="p-2">면접 질문</th>
              <th className="p-2">질문 생성일</th>
              <th className="p-2">복습 횟수</th> {/* ✅ 복습 횟수 헤더 추가 */}
              <th className="p-0">삭제</th>
            </tr>
          </thead>
          <tbody>
            {filteredData.map((item: Qna) => (
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
                <td className="p-2 text-center">{item.reviewCount}</td>{" "}
                {/* ✅ 복습 횟수 표시 */}
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
                <td colSpan={4} className="p-4 text-center text-gray-400">
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
