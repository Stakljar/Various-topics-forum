import { useContext, useEffect, useRef, useState } from "react"
import { Link, Outlet, useNavigate } from "react-router-dom"
import { AuthContext } from "../App"
import Button from "../components/Button"
import useSignout from "../hooks/useSignout"
import { roles } from "../data/roles"

export default function Main() {
  const { user } = useContext(AuthContext)
  const optionsRef = useRef()
  const signout = useSignout()
  const navigate = useNavigate()
  const [isMenuVisible, setIsMenuVisible] = useState(false)
  const isMenuVisibleRef = useRef(false)

  useEffect(() => {
    function cancelOptions(e) {
      if (isMenuVisibleRef.current && optionsRef.current !== e.target && !optionsRef.current?.contains(e.target)) {
        isMenuVisibleRef.current = false
        setIsMenuVisible(false)
      }
    }
    window.addEventListener("click", cancelOptions)
    return () => window.removeEventListener("click", cancelOptions)
  }, [])

  return (
    <>
      <nav>
        <h1 className="title" onClick={() => navigate("/")}>Topics Unraveled</h1>
        <div>
          {
            user.role === roles.GUEST ? <Link to="/signin"><Button text="Sign in" /></Link> :
              <svg ref={optionsRef} viewBox="0 0 24 24" fill="none"
                onClick={() => { isMenuVisibleRef.current = !isMenuVisibleRef.current; setIsMenuVisible((prev) => !prev) }} xmlns="http://www.w3.org/2000/svg">
                <path d="M0 4H24M0 12H24M0 20H24" strokeWidth={3} />
              </svg>
          }
          {
            isMenuVisible && (
              <div className="menu">
                <ul>
                  <li onClick={() => navigate(`/profile/${user.id}`)}>My Profile</li>
                  <li onClick={() => navigate("/change-password")}>Change Password</li>
                  <li onClick={signout}>Sign Out</li>
                </ul>
              </div>)
          }
        </div>
      </nav>
      <Outlet />
    </>
  )
}
