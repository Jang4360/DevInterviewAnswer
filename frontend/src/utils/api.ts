import axios from "axios";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL;
const api = axios.create({
  baseURL: `${API_BASE_URL}/api`,
});

// 요청 시 토큰 자동 삽입
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`; // 꼭 Bearer 포함
  }
  return config;
});

// 응답 시 401 에러 → 로그인 페이지로 이동
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("userId");
      window.location.href = "/login"; // router 접근 불가한 곳이므로 window 사용
    }
    return Promise.reject(err);
  }
);

export default api;
