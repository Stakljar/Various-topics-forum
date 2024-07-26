import { useEffect, useState } from "react"
import { useNavigate, useSearchParams } from "react-router-dom"
import axiosInstance from "../../axios/axiosInstances"
import Spinner from "../../components/Spinner"
import InfoMessage from "../../components/InfoMessage"
import Button from "../../components/Button"
import useErrorMessage from "../../hooks/useErrorMessage"
import { AxiosError } from "axios"

export default function EmailConfirmation() {
  const [searchParams] = useSearchParams()
  const [errorMessage, handleError] = useErrorMessage()
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(true)
  const [isResendLoading, setIsResendLoading] = useState(false)
  const [errorResendMessage, handleResendError, resetResendError] = useErrorMessage()

  useEffect(() => {
    if (!isLoading) {
      return
    }
    const abortController = new AbortController()
    const confirmEmail = async () => {
      try {
        await axiosInstance.post(`/auth/signup/confirm`,
          {
            email: searchParams.get("email"),
            token: searchParams.get("token"),
          },
          {
            signal: abortController.signal,
          })
        setIsLoading(false)
      }
      catch (error) {
        console.log(error)
        if (error.code === AxiosError.ERR_CANCELED) {
          return
        }
        setIsLoading(false)
        handleError(error)
      }
    }
    confirmEmail()
    return () => abortController.abort()
  }, [isLoading])

  useEffect(() => {
    if (!isResendLoading) {
      return
    }
    const abortController = new AbortController()
    resetResendError()
    const resendEmail = async () => {
      try {
        await axiosInstance.post("/auth/signup/resend-email",
          { email: searchParams.get("email") },
          {
            signal: abortController.signal,
          })
        setIsResendLoading(false)
        navigate("/notice", { replace: true, state: "An e-mail has been sent to you to confirm your e-mail address." })
      }
      catch (error) {
        setIsResendLoading(false)
        handleResendError(error)
      }
    }
    resendEmail()
    return () => abortController.abort()
  }, [isResendLoading])

  return (
    <div id="email-confirmation">
      <h2>
        {isLoading ? <Spinner /> : errorMessage ? <InfoMessage infoMessage={errorMessage} /> : <InfoMessage infoMessage={"E-mail has been confirmed successfully."} />}
      </h2>
      {
        errorMessage && errorMessage !== "E-mail has already been confirmed" &&
        <div>
          <Button text="Resend token" onClick={() => setIsResendLoading(true)} />
          {isResendLoading && <Spinner />}
        </div>
      }
      {errorResendMessage && <InfoMessage infoMessage={errorResendMessage} />}
    </div>
  )
}
