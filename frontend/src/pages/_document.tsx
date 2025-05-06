// src/pages/_document.tsx
import { Html, Head, Main, NextScript } from "next/document";

export default function Document() {
  return (
    <Html lang="ko">
      <Head>
        {/* ✅ 모바일 반응형 필수 메타태그 */}
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        {/* 필요한 경우 아래 메타도 추가 가능 */}
        <meta charSet="UTF-8" />
        <meta name="theme-color" content="#212121" />
      </Head>
      <body className="bg-[#212121] text-white">
        <Main />
        <NextScript />
      </body>
    </Html>
  );
}
