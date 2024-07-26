import "./css/App.css"
import SignIn from "./pages/auth/SignIn"
import SignUp from "./pages/auth/SignUp"
import Main from "./pages/Main"
import { BrowserRouter, Routes, Route } from "react-router-dom"
import Discussions from "./pages/discussion/Discussions"
import { createContext, useState } from "react"
import SignInNotice from "./pages/auth/SignInNotice"
import NewDiscussion from "./pages/discussion/NewDiscussion"
import PasswordChange from "./pages/user/PasswordChange"
import Error from "./pages/Error"
import Discussion from "./pages/discussion/Discussion"
import Profile from "./pages/user/Profile"
import PasswordReset from "./pages/auth/PasswordReset"
import EmailConfirmation from "./pages/auth/EmailConfirmation"
import NewPassword from "./pages/auth/NewPassword"
import Notice from "./pages/Notice"
import Redirect from "./security/Redirect"
import ProtectedRoute from "./security/ProtectedRoute"
import Validate from "./security/Validate"
import { roles } from "./data/roles"

export const AuthContext = createContext(null)

export default function App() {
  const [user, setUser] = useState({ id: null, role: roles.GUEST, accessToken: null })

  return (
    <div className="App">
      <AuthContext.Provider value={{ user, setUser }}>
        <BrowserRouter>
          <Routes>
            <Route element={<Validate />}>
              <Route element={<Redirect />}>
                <Route path="/signin" >
                  <Route index element={<SignIn />} />
                  <Route path="/signin/signin-notice" element={<SignInNotice />} />
                </Route>
                <Route path="/signup" element={<SignUp />} />
                <Route path="/new-password" element={<NewPassword />} />
                <Route path="/confirm-email" element={<EmailConfirmation />} />
                <Route path="/reset-password" element={<PasswordReset />} />
              </Route>
              <Route path="/notice" element={<Notice />} />
              <Route path="/" element={<Main />}>
                <Route index element={<Discussions />} />
                <Route path="/discussions" element={<Discussions />} />
                <Route path="/profile/:userId" element={<Profile />} />
                <Route path="/new-discussion" element={
                  <ProtectedRoute permittedRoles={[roles.REGULAR_USER]}>
                    <NewDiscussion />
                  </ProtectedRoute>
                } />
                <Route path="/discussions/:discussionId" element={<Discussion />} />
                <Route path="/change-password" element={
                  <ProtectedRoute permittedRoles={[roles.ADMIN, roles.REGULAR_USER]} >
                    <PasswordChange />
                  </ProtectedRoute>
                } />
              </Route>
              <Route path="*" element={<Error text="Page not found" />} />
              <Route path="/unauthorized" element={<Error text="You are unauthorized to view this page." />} />
              <Route path="/forbidden" element={<Error text="You are forbidden to view this page." />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </AuthContext.Provider>
    </div>
  )
}
