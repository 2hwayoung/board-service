import RequireAuthenticated from "@/components/auth/RequireAuthenticated";
import ClientPage from "./clientPage";

export default async function Page() {
  return (
    <RequireAuthenticated>
      <ClientPage />
    </RequireAuthenticated>
  );
}
