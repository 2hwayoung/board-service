import ClientPage from "./clientPage";
import client from "@/lib/backend/client";

export default async function Page({
  searchParams,
}: {
  searchParams: {
    keywordType?: "TITLE" | "CONTENT";
    keyword: string;
    pageSize: number;
    page: number;
  };
}) {
  const {
    keywordType = "TITLE",
    keyword = "",
    pageSize = 10,
    page = 1,
  } = await searchParams;

  const response = await client.GET("/api/v1/posts", {
    params: {
      query: {
        keyword,
        keywordType,
        pageSize,
        page,
      },
    },
  });

  const rsData = response.data!!;

  return (
    <ClientPage
      rsData={rsData}
      pageSize={pageSize}
      keyword={keyword}
      keywordType={keywordType}
      page={page}
    />
  );
}
