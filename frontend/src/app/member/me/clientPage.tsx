"use client";

export default function ClientPage({
  me,
}: {
  me: {
    id: number;
    nickname: string;
  };
}) {
  return (
    <>
      <div>내정보 페이지</div>
      <div>번호 : {me.id}</div>
      <div>닉네임 : {me.nickname}</div>
    </>
  );
}
