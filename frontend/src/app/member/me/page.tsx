import { cookies } from "next/headers";
import ClientPage from "./clientPage";
import client from "@/lib/backend/client";
import { RequestCookie } from "next/dist/compiled/@edge-runtime/cookies";

export default async function Page() {
  const myCookie = await cookies();
  const { isLogin, payload } = parseAccessToken(myCookie.get("accessToken"));

  if (!isLogin) {
    return <div>로그인 필요</div>;
  }

  const me = {
    id: payload.id,
    nickname: payload.nickname,
  };

  return <ClientPage me={me} />;
}

function parseAccessToken(accessToken: RequestCookie | undefined) {
  let isExpired = true;
  let payload = null;

  if (accessToken) {
    try {
      const tokenParts = accessToken.value.split(".");
      payload = JSON.parse(Buffer.from(tokenParts[1], "base64").toString());
      const expTimestamp = payload.exp * 1000; // exp는 초 단위이므로 밀리초로 변환
      isExpired = Date.now() > expTimestamp;
    } catch (e) {
      console.error("토큰 파싱 중 오류 발생:", e);
    }
  }

  let isLogin = payload !== null;

  return { isLogin, isExpired, payload };
}
