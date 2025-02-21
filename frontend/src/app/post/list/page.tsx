import { paths } from "@/lib/backend/apiV1/schema";
import createClient from "openapi-fetch";

const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
});

export default async function Page({
  searchParams,
}: {
  searchParams: {
    keywordType?: "TITLE" | "CONTENT";
    keyword: string;
  };
}) {
  const { keywordType = "TITLE", keyword = "" } = await searchParams;

  const response = await client.GET("/api/v1/posts", {
    params: {
      query: {
        keyword,
        keywordType,
      },
    },
  });

  const rsData = response.data!!;
  const pageDto = rsData.data;

  return (
    <div>
      <div>응답 코드 : {rsData.code}</div>
      <div>결과 메시지 : {rsData.msg}</div>

      <div>totalPages : {pageDto.totalPages}</div>
      <div>totalItems : {pageDto.totalItems}</div>
      <div>currentPageNo : {pageDto.currentPageNo}</div>
      <div>pageSize : {pageDto.pageSize}</div>

      <hr />

      <form>
        <select name="keywordType" defaultValue={keywordType}>
          <option value="TITLE">제목</option>
          <option value="CONTENT">내용</option>
        </select>
        <input
          placeholder="검색어 입력"
          type="text"
          name="keyword"
          defaultValue={keyword}
        />
        <input type="submit" value="검색" />
      </form>

      <ul>
        {pageDto.items.map((item) => {
          return (
            <li className="border-2 border-red-500 my-2 p-2" key={item.id}>
              <div>id : {item.id}</div>
              <div>title : {item.title}</div>
              <div>authorId : {item.authorId}</div>
              <div>authorName : {item.authorName}</div>
              <div>published : {`${item.published}`}</div>
              <div>listed : {`${item.listed}`}</div>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
