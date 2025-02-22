"use client";

import { LoginMemberContext } from "@/stores/auth/loginMemberStore";
import { use } from "react";

export default function ClientPage() {
  const { loginMember } = use(LoginMemberContext);

  return (
    <>
      <div>내정보 페이지</div>
      <div>번호 : {loginMember.id}</div>
      <div>닉네임 : {loginMember.nickname}</div>
    </>
  );
}
