"use client";

import client from "@/lib/backend/client";
import Link from "next/link";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHouse } from "@fortawesome/free-solid-svg-icons";
import {
  LoginMemberContext,
  useLoginMember,
} from "@/stores/auth/loginMemberStore";
import { useRouter } from "next/navigation";

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { ModeToggle } from "@/components/ui/custom/DartModeToggle";

export default function ClientLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const router = useRouter();
  const {
    setLoginMember,
    isLogin,
    loginMember,
    removeLoginMember,
    isLoginMemberPending,
    isAdmin,
    setNoLoginMember,
  } = useLoginMember();

  const loginMemberContextValue = {
    loginMember,
    setLoginMember,
    removeLoginMember,
    isLogin,
    isLoginMemberPending,
    isAdmin,
    setNoLoginMember,
  };

  async function handleLogout(e: React.MouseEvent<HTMLAnchorElement>) {
    e.preventDefault();
    const response = await client.DELETE("/api/v1/members/logout", {
      credentials: "include",
    });

    if (response.error) {
      alert(response.error.msg);
      return;
    }

    removeLoginMember();
    router.replace("/");
    // window.location.href = "/post/list";
  }

  return (
    <>
      <LoginMemberContext.Provider value={loginMemberContextValue}>
        <header className="flex justify-end gap-3 px-4">
          <DropdownMenu>
            <DropdownMenuTrigger>
              <FontAwesomeIcon icon={faHouse} />
              Home
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              <DropdownMenuLabel>{loginMember.nickname}</DropdownMenuLabel>
              <DropdownMenuSeparator />

              <DropdownMenuItem>
                <Link href="/">메인</Link>
              </DropdownMenuItem>

              <DropdownMenuItem>
                <Link href="/about">소개</Link>
              </DropdownMenuItem>

              <DropdownMenuItem>
                <Link href="/post/list">글 목록</Link>
              </DropdownMenuItem>
              {isLogin && (
                <DropdownMenuItem>
                  <Link href="/post/write">글 작성</Link>
                </DropdownMenuItem>
              )}
              {!isLogin && (
                <DropdownMenuItem>
                  <Link href="/adm/member/login">관리자 로그인</Link>
                </DropdownMenuItem>
              )}
              {!isLogin && (
                <DropdownMenuItem>
                  <Link href="/member/join">회원 가입</Link>
                </DropdownMenuItem>
              )}
              {isLogin && (
                <DropdownMenuItem>
                  <Link href="" onClick={handleLogout}>
                    로그아웃
                  </Link>
                </DropdownMenuItem>
              )}
              {isLogin && (
                <DropdownMenuItem>
                  <Link href="/member/me">내정보</Link>
                </DropdownMenuItem>
              )}
            </DropdownMenuContent>
          </DropdownMenu>
          <ModeToggle />
        </header>
        <div className="flex flex-col flex-grow justify-center items-center">
          {children}
        </div>

        <footer className="flex justify-center gap-7 p-4">
          <Link href="/adm">관리자</Link>
          <Link href="/adm/member/login">관리자 로그인</Link>
          <Link href="/member/me">내 정보</Link>
        </footer>
      </LoginMemberContext.Provider>
    </>
  );
}
