import { useContext, useEffect } from "react"
import axiosInstance, { interceptInstance } from "../axios/axiosInstances"
import { AuthContext } from "../App"
import { HttpStatusCode } from "axios"
import useSignout from "./useSignout"

const useRefreshIntercept = () => {
  const interceptedInstance = interceptInstance
  const { user, setUser } = useContext(AuthContext)
  const signout = useSignout()
  useEffect(() => {
    interceptedInstance.interceptors.request.use(
      config => {
        if (!config.headers["Authorization"]) {
          config.headers["Authorization"] = "Bearer " + user.accessToken
        }
        return config
      },
      error => {
        Promise.reject(error)
      }
    )
    interceptedInstance.interceptors.response.use(
      response => response,
      async error => {
        const originalRequest = error.config
        if (error.response?.status === HttpStatusCode.Unauthorized && !originalRequest?.sent) {
          originalRequest.sent = true
          try {
            const response = await axiosInstance.get(`/auth/refresh-token`, {
              withCredentials: true
            })
            setUser({ id: response.data.id, role: response.data.role, accessToken: response.data.accessToken })
            originalRequest.headers["Authorization"] = `Bearer ${response.data.accessToken}`
            return interceptedInstance(originalRequest)
          }
          catch (err) {
            if (err.response?.status === HttpStatusCode.Unauthorized) {
              signout()
              return new Promise(() => {})
            }
            return Promise.reject(err)
          }
        }
        else {
          return Promise.reject(error)
        }
      }
    )

    return () => {
      interceptedInstance.interceptors.request.handlers.pop()
      interceptedInstance.interceptors.response.handlers.pop()
    }
  }, [user])

  return interceptedInstance
}

export default useRefreshIntercept
