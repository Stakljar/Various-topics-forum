import { useEffect, useState } from "react"
import Button from "../../components/Button"
import useErrorMessage from "../../hooks/useErrorMessage"
import Spinner from "../../components/Spinner"
import InfoMessage from "../../components/InfoMessage"
import { useNavigate, useSearchParams } from "react-router-dom"
import axiosInstance from "../../axios/axiosInstances"

export default function NewPassword() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const [errorMessage, handleError, resetError] = useErrorMessage()
  const [password, setPassword] = useState("")
  const [isLoading, setIsLoading] = useState(false)

  useEffect(() => {
    if (!isLoading) {
      return
    }
    const abortController = new AbortController()
    resetError()
    const resetPassword = async () => {
      try {
        await axiosInstance.put("/auth/reset-password",
          {
            email: searchParams.get("email"),
            token: searchParams.get("token"),
            newPassword: password
          },
          {
            signal: abortController.signal,
          })
        navigate("/notice", { replace: true, state: "You have successfully reset your password." })
      }
      catch (error) {
        setIsLoading(false)
        handleError(error)
      }
    }
    resetPassword()
    return () => abortController.abort()
  }, [isLoading])

  return (
    <div id="new-password">
      <form onSubmit={(e) => { e.preventDefault(); setIsLoading(true) }}>
        <div>
          <label htmlFor="password-input"><strong>New password: </strong></label>
          <input id="password-input" className="default-input" type="password" name="password"
            value={password} onChange={(e) => setPassword(e.target.value)} minLength="8" />
        </div>
        <div>
          <Button text="Confirm" type="submit" />
        </div>
      </form>
      {isLoading && <Spinner />}
      {errorMessage && <InfoMessage infoMessage={errorMessage} />}
    </div>
  )
}
