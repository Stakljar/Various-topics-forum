import { useContext, useEffect, useState } from "react"
import { Outlet } from "react-router-dom"
import Spinner from "../components/Spinner"
import { AuthContext } from "../App"
import axiosInstance from "../axios/axiosInstances"
import useSignout from "../hooks/useSignout"
import { HttpStatusCode } from "axios"

export default function Validate() {
  const { setUser } = useContext(AuthContext)
  const [isLoading, setIsLoading] = useState(true)
  const signout = useSignout()

  useEffect(() => {
    if (!isLoading) {
      return
    }
    const refresh = async () => {
      try {
        const response = await axiosInstance.get("/auth/refresh-token", {
          withCredentials: true
        })
        setIsLoading(false)
        setUser({ id: response.data.id, role: response.data.role, accessToken: response.data.accessToken })
      }
      catch (error) {
        setIsLoading(false)
        if (error.response?.data?.message === "Token not supplied") {
          return
        }
        else if (error.response?.status === HttpStatusCode.Unauthorized) {
          signout()
          return
        }
      }
    }
    refresh()
  }, [isLoading])

  return (
    <>
      {isLoading ? <Spinner /> : <Outlet />}
    </>
  )
}
