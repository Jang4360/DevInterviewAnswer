import { useRouter } from "next/router";

export default function MyPage() {
  const router = useRouter();

  const handleLogout = () => {
    // TODO: 실제 로그아웃 처리 필요
    router.push("/login");
  };

  return (
    <div className="p-6 text-white">
      <h2 className="text-2xl font-bold mb-6">마이페이지</h2>

      <div className="bg-[#303030] p-6 rounded-lg mb-6">
        <p>누적 질문 수: 12개</p>
        <p>누적 복습 횟수: 25회</p>
        <p>최근 복습일: 2025/04/20</p>
      </div>

      <button
        onClick={handleLogout}
        className="border border-white px-4 py-2 rounded"
      >
        로그아웃
      </button>
    </div>
  );
}
