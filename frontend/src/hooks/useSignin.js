import { useContext, useEffect } from "react"
import { AuthContext } from "../App"
import axiosInstance from "../axios/axiosInstances"
import { useNavigate } from "react-router-dom"
import useErrorAlert from "./useErrorAlert"
import { roles } from "../data/roles"

const useSignin = () => {
  const channel = new BroadcastChannel("signin-channel")
  const errorAlert = useErrorAlert()
  const { setUser } = useContext(AuthContext)
  const navigate = useNavigate()

  useEffect(() => {
    const signin = async () => {
      try {
        await axiosInstance.post("/auth/signout", {
          withCredentials: true,
        })
        setUser({ id: response.data.id, role: response.data.role, accessToken: response.data.accessToken })
      }
      catch (error) {
        errorAlert(error)
      }
    }
    channel.onmessage = () => {
      if (signin() === "success") {
        channel.close()
      }
    }
  }, [])

  const performSignin = () => channel.postMessage("signin")
  return performSignin
}

export default useSignin
