import { useEffect, useState } from "react"
import { useNavigate, useSearchParams } from "react-router-dom"
import axiosInstance from "../../axios/axiosInstances"
import Spinner from "../../components/Spinner"
import useErrorMessage from "../../hooks/useErrorMessage"
import Button from "../../components/Button"
import InfoMessage from "../../components/InfoMessage"

export default function SignInNotice() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const [errorMessage, handleError, resetError] = useErrorMessage()
  const [isLoading, setIsLoading] = useState(false)

  useEffect(() => {
    if (!isLoading) {
      return
    }
    const abortController = new AbortController()
    resetError()
    const resendEmail = async () => {
      try {
        await axiosInstance.post(`/auth/signup/resend-email`,
          { email: searchParams.get("email") },
          {
            signal: abortController.signal,
          })
        setIsLoading(false)
        navigate("/notice", { replace: true, state: "An e-mail has been sent to you to confirm your e-mail address." })
      }
      catch (error) {
        setIsLoading(false)
        handleError(error)
      }
    }
    resendEmail()
    return () => abortController.abort()
  }, [isLoading])

  return (
    <div id="signin-notice">
      <h2>You need to confirm your e-mail to register your account.</h2>
      <Button text="Resend e-mail" onClick={() => setIsLoading(true)} />
      {!isLoading || <Spinner />}
      {errorMessage && <InfoMessage infoMessage={errorMessage} />}
    </div>
  )
}
