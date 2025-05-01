"use client";

import React, { useState } from "react";
import Input from "@/components/common/Input";
import Button from "@/components/common/Button";
import { useRouter } from "next/navigation";
import Link from "next/link";
import api from "../utils/api";

export default function SignupPage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [emailError, setEmailError] = useState(false);
  const [passwordMismatch, setPasswordMismatch] = useState(false);

  const router = useRouter();

  const handleSignup = async () => {
    setEmailError(false);
    setPasswordMismatch(false);

    if (!email.includes("@")) {
      setEmailError(true);
      return;
    }

    if (password !== confirmPassword) {
      setPasswordMismatch(true);
      return;
    }

    try {
      await api.post("/users", {
        name,
        email,
        password,
      });
      alert("회원가입 완료! 로그인 해주세요.");
      router.push("/login");
    } catch (error) {
      alert(
        "회원가입 실패: " +
          ((error as any).response?.data?.message || "서버 오류")
      );
    }
  };

  return (
    <div className="flex justify-center items-center h-screen bg-[#212121]">
      <div className="bg-[#171717] p-8 rounded-lg w-full max-w-md text-white">
        <h1 className="text-2xl font-bold text-center mb-6">Dev Interview</h1>

        <Input
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="이름"
          className="mb-4 w-full bg-[#303030]"
        />
        <Input
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="이메일"
          className="mb-4 w-full bg-[#303030]"
        />
        <Input
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="비밀번호"
          type="password"
          className="mb-4 w-full bg-[#303030]"
        />
        <Input
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          placeholder="비밀번호 확인"
          type="password"
          className="mb-4 w-full bg-[#303030]"
        />

        {passwordMismatch && (
          <p className="text-red-500 text-sm mb-4">비밀번호가 다릅니다.</p>
        )}

        <Button onClick={handleSignup} className="w-full bg-[#303030]">
          회원가입
        </Button>

        <p className="text-center text-gray-400 mt-4">
          이미 계정이 있으신가요?{" "}
          <Link href="/login" className="underline text-white">
            로그인
          </Link>
        </p>
      </div>
    </div>
  );
}
