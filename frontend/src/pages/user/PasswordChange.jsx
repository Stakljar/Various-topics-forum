import { useContext, useEffect, useState } from "react"
import Button from "../../components/Button"
import useErrorMessage from "../../hooks/useErrorMessage"
import Spinner from "../../components/Spinner"
import InfoMessage from "../../components/InfoMessage"
import axiosInstance from "../../axios/axiosInstances"
import { useNavigate } from "react-router-dom"
import { AuthContext } from "../../App"
import useRefreshIntercept from "../../hooks/useRefreshIntercept"

export default function PasswordChange() {
  const { user } = useContext(AuthContext)
  const navigate = useNavigate()
  const interceptedInstance = useRefreshIntercept()
  const [errorMessage, handleError, resetError] = useErrorMessage()
  const [isLoading, setIsLoading] = useState(false)
  const [values, setValues] = useState({ oldPassword: "", newPassword: "", newPasswordConfirmed: "" })

  useEffect(() => {
    if (!isLoading) {
      return
    }
    const abortController = new AbortController()
    resetError()
    const resetPassword = async () => {
      try {
        await interceptedInstance.put("/users/user/change-password",
          {
            oldPassword: values.oldPassword,
            newPassword: values.newPassword,
          },
          {
            headers: {
              "Authorization": "Bearer " + user.accessToken,
            },
            signal: abortController.signal,
          })
        navigate("/", { replace: true })
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
    <div id="password-change">
      <form onSubmit={(e) => {
        e.preventDefault()
        if (values.newPassword !== values.newPasswordConfirmed) {
          alert("Passwords do not match.")
          return
        }
        if (values.newPassword === values.oldPassword) {
          alert("Password must be different than old password.")
          return
        }
        setIsLoading(true)
      }}>
        <div>
          <label htmlFor="old-password-input"><strong>Old password: </strong></label>
          <input id="old-password-input" className="default-input" type="password" name="oldPassword"
            value={values.oldPassword} onChange={(e) => setValues((prev) => { return { ...prev, [e.target.name]: e.target.value } })} required />
        </div>
        <div>
          <label htmlFor="new-password-input"><strong>New password: </strong></label>
          <input id="new-password-input" className="default-input" type="password" name="newPassword"
            value={values.newPassword} onChange={(e) => setValues((prev) => { return { ...prev, [e.target.name]: e.target.value } })} minLength="8" />
        </div>
        <div>
          <label htmlFor="new-password-confirm-input"><strong>Cofirm new password: </strong></label>
          <input id="new-password-confirm-input" className="default-input" type="password" name="newPasswordConfirmed"
            value={values.newPasswordConfirmed} onChange={(e) => setValues((prev) => { return { ...prev, [e.target.name]: e.target.value } })} minLength="8" />
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
