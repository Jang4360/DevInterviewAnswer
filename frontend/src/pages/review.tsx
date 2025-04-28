"use client";

import React, { useState, useEffect } from "react";
import Input from "@/components/common/Input";
import Modal from "@/components/common/Modal";
import { FiTrash2 } from "react-icons/fi";
import Link from "next/link";
import api from "../utils/api";

export default function ReviewPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [data, setData] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedId, setSelectedId] = useState<string | null>(null);

  useEffect(() => {
    const fetchQnAs = async () => {
      const userId = localStorage.getItem("userId");
      try {
        const res = await api.get(`/qna/user/${userId}`);
        console.log("서버에서 받은 데이터:", res.data);
        setData(res.data);
      } catch (error) {
        console.error(error);
        alert("QnA 목록 불러오기 실패");
      }
    };

    fetchQnAs();
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
      <div className="bg-[#303030] rounded-lg p-4">
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
                      className="text-white hover:text-red-500"
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
