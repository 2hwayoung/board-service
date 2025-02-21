import { components } from "@/lib/backend/apiV1/schema";

type PostDto = components["schemas"]["PostDto"];
type PostItemPageDto = components["schemas"]["PageDto"];

export default async function Page({
  searchParams,
}: {
  searchParams: {
    keywordType?: "TITLE" | "CONTENT";
    keyword: string;
  };
}) {
  const { keywordType = "TITLE", keyword = "" } = await searchParams;

  const response = await fetch(
    `http://localhost:8080/api/v1/posts?keywordType=${keywordType}&keyword=${keyword}`
  );

  if (!response.ok) {
    throw new Error("에러");
  }

  const rsData = await response.json();
  const pageDto: PostItemPageDto = rsData.data;

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
          <option value="title">제목</option>
          <option value="content">내용</option>
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
        {pageDto.items.map((item: PostDto) => {
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
