import React from "react";

type InputProps = {
  type?: string;
  placeholder?: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  className?: string;
};

export default function Input({
  type = "text",
  placeholder = "",
  value,
  onChange,
  className = "",
}: InputProps) {
  return (
    <input
      type={type}
      placeholder={placeholder}
      value={value}
      onChange={onChange}
      className={`bg-[#303030] text-white px-4 py-2 rounded outline-none placeholder-[#BBB5B5] ${className}`}
    />
  );
}
