import { useContext, useEffect } from "react"
import { AuthContext } from "../App"
import { Outlet, useNavigate } from "react-router-dom"
import { roles } from "../data/roles"

export default function Redirect() {
  const { user } = useContext(AuthContext)
  const navigate = useNavigate()

  useEffect(() => {
    if (user.role !== roles.GUEST) {
      navigate("/")
    }
  }, [])

  return (
    <Outlet />
  )
}
