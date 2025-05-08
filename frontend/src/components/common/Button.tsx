import React from "react";

type ButtonProps = {
  children: React.ReactNode;
  onClick?: () => void;
  type?: "button" | "submit" | "reset";
  className?: string;
};
//
export default function Button({
  children,
  onClick,
  type = "button",
  className = "",
}: ButtonProps) {
  return (
    <button
      type={type}
      onClick={onClick}
      className={`bg-[#303030] text-white px-4 py-2 rounded hover:bg-[#404040] transition ${className}`}
    >
      {children}
    </button>
  );
}
