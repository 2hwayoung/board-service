import RequireAdmin from "@/components/auth/RequireAdmin";
import ClientPage from "./clientPage";

export default function Page() {
  return (
    <RequireAdmin>
      <ClientPage />
    </RequireAdmin>
  );
}
