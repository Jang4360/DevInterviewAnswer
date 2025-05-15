// stores/useTodayReviews.ts
import { create } from "zustand";
import api from "@/utils/api";

interface Qna {
  id: string;
  question: string;
  isDeleted?: boolean;
  // 필요하면 scheduleDate 등 추가
}

interface TodayReviewStore {
  reviews: Qna[];
  fetchReviews: (userId: string) => Promise<void>;
  removeReviewById: (id: string) => void;
}

const useTodayReviews = create<TodayReviewStore>((set) => ({
  reviews: [],
  fetchReviews: async (userId: string) => {
    try {
      const res = await api.get(`/qna/today?userId=${userId}`);
      const filteredReviews = res.data.filter((qna: Qna) => !qna.isDeleted);
      set({ reviews: filteredReviews });
    } catch (error) {
      console.error("오늘 복습 목록 로딩 실패", error);
    }
  },
  removeReviewById: (id) =>
    set((state) => ({
      reviews: state.reviews.filter((qna) => qna.id !== id),
    })),
}));

export default useTodayReviews;
