import { useContext } from "react"
import { AuthContext } from "../App"
import axiosInstance from "../axios/axiosInstances"
import { useNavigate } from "react-router-dom"
import useErrorAlert from "./useErrorAlert"
import { roles } from "../data/roles"

const useSignout = () => {
  const errorAlert = useErrorAlert()
  const { setUser } = useContext(AuthContext)
  const navigate = useNavigate()

  const signout = async () => {
    try {
      await axiosInstance.post("/auth/signout", {
        withCredentials: true,
      })
      setUser({ id: null, role: roles.GUEST, accessToken: null })
      navigate("/signin")
    }
    catch (error) {
      errorAlert(error)
    }
  }
  return signout
}

export default useSignout
