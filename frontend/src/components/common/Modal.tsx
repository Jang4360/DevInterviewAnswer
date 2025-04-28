// src/components/common/Modal.tsx
"use client";

interface ModalProps {
  isOpen: boolean;
  onConfirm: () => void;
  onCancel: () => void;
  message: string;
}

export default function Modal({
  isOpen,
  onConfirm,
  onCancel,
  message,
}: ModalProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-[#303030] p-6 rounded-lg text-white">
        <p className="mb-4">{message}</p>
        <div className="flex justify-end space-x-4">
          <button
            onClick={onCancel}
            className="px-4 py-2 bg-gray-600 rounded hover:bg-gray-700"
          >
            아니오
          </button>
          <button
            onClick={onConfirm}
            className="px-4 py-2 bg-red-500 rounded hover:bg-red-600"
          >
            예
          </button>
        </div>
      </div>
    </div>
  );
}
