"use client";

import { components } from "@/lib/backend/apiV1/schema";
import Link from "next/link";

export default function ClientPage({
  post,
}: {
  post: components["schemas"]["PostWithContentDto"];
}) {
  return (
    <>
      <div>
        <div>번호 : {post.id}</div>
        <div>제목 : {post.title}</div>
        <div>내용 : {post.content}</div>
        <div>작성자 번호 : {post.authorId} </div>
        <div>작성자 닉네임 : {post.authorName} </div>
        <div>등록일 : {post.createdDate}</div>
        <div>수정일 : {post.modifiedDate}</div>
        <div>공개 여부 : {`${post.published}`}</div>
        <div>리스팅 여부 : {`${post.listed}`}</div>
      </div>
      <div>
        {post.canActorHandle && (
          <div>
            <Link href={`/post/edit/${post.id}`}>수정</Link>
            <Link href={`/post/delete/${post.id}`}>삭제</Link>
          </div>
        )}
      </div>
    </>
  );
}
