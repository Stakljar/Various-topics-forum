import { useEffect, useState } from "react"
import Button from "../../components/Button"
import useErrorMessage from "../../hooks/useErrorMessage"
import Spinner from "../../components/Spinner"
import InfoMessage from "../../components/InfoMessage"
import axiosInstance from "../../axios/axiosInstances"
import { useNavigate } from "react-router-dom"
import { HttpStatusCode } from "axios"

export default function PasswordReset() {
  const navigate = useNavigate()
  const [errorMessage, handleError, resetError, setErrorMessage] = useErrorMessage()
  const [email, setEmail] = useState("")
  const [isLoading, setIsLoading] = useState(false)

  useEffect(() => {
    if (!isLoading) {
      return
    }
    const abortController = new AbortController()
    resetError()
    const sendPasswordResetEmail = async () => {
      try {
        await axiosInstance.post("/auth/send-password-reset-email",
          { email },
          {
            signal: abortController.signal,
          })
        navigate("/notice", { replace: true, state: "An e-mail has been sent to you to reset your password." })
      }
      catch (error) {
        setIsLoading(false)
        if(error.response?.status === HttpStatusCode.NotFound) {
          setErrorMessage("E-mail address not found")
        }
        else {
          handleError(error)
        }
      }
    }
    sendPasswordResetEmail()
    return () => abortController.abort()
  }, [isLoading])

  return (
    <div id="password-reset">
      <h3>Provide e-mail for password reset</h3>
      <form onSubmit={(e) => { e.preventDefault(); setIsLoading(true) }}>
        <div>
          <label htmlFor="email-input"><strong>Email: </strong></label>
          <input id="email-input" className="default-input" type="email" name="email"
            value={email} onChange={(e) => setEmail(e.target.value)} required />
        </div>
        <Button text="Confirm" type="submit" />
      </form>
      {isLoading && <Spinner />}
      {errorMessage && <InfoMessage infoMessage={errorMessage} />}
    </div>
  )
}
