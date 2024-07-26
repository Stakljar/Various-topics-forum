import { useEffect, useState } from "react"
import axiosInstance from "../../axios/axiosInstances"
import { useNavigate } from "react-router-dom"
import EmailAndPasswordInput from "../../components/EmailAndPasswordInput"
import Spinner from "../../components/Spinner"
import { HttpStatusCode } from "axios"
import useErrorMessage from "../../hooks/useErrorMessage"
import InfoMessage from "../../components/InfoMessage"
import Button from "../../components/Button"

export default function SignUp() {
  const navigate = useNavigate()
  const [errorMessage, handleError, resetError, setErrorMessage] = useErrorMessage()
  const [isLoading, setIsLoading] = useState(false)
  const [inputNextInformation, setInputNextInformation] = useState(false)
  const [credentials, setCredentials] = useState({ email: "", password: "", profileName: "" })

  useEffect(() => {
    if (!isLoading) {
      return
    }
    const abortController = new AbortController()
    resetError()
    const signup = async () => {
      try {
        await axiosInstance.post("/auth/signup",
          credentials,
          {
            signal: abortController.signal,
          })
        navigate("/notice", { replace: true, state: "An e-mail has been sent to you to confirm your e-mail address." })
      }
      catch (error) {
        setIsLoading(false)
        if (error.response?.status === HttpStatusCode.Conflict) {
          setErrorMessage("User already exists")
        }
        else {
          handleError(error)
        }
      }
    }
    signup()
    return () => abortController.abort()
  }, [isLoading])

  return (
    <div id="signup">
      <h1>Topics Unraveled</h1>
      <hr />
      {
        !inputNextInformation ?
          <form onSubmit={(e) => { e.preventDefault(); setInputNextInformation(true) }}>
            <EmailAndPasswordInput credentials={credentials} setCredentials={setCredentials} type="signup" />
            <div>
              <Button text="Next" type="submit" />
            </div>
          </form> :
          <>
            <form onSubmit={(e) => { e.preventDefault(); setIsLoading(true) }}>
              <div>
                <label htmlFor="signup-profile-name-input"><strong>Profile name: </strong></label>
                <input id="signup-profile-name-input" className="default-input" type="text" name="profileName" value={credentials.profileName}
                  onChange={(e) => setCredentials((prev) => { return { ...prev, [e.target.name]: e.target.value } })} required />
              </div>
              <div>
                <Button text="Register" type="submit" />
              </div>
            </form>
            {isLoading && <Spinner />}
            {errorMessage && <InfoMessage infoMessage={errorMessage} />}
            <div id="signup-back-button" >
              <Button text="Back" onClick={() => setInputNextInformation(false)} />
            </div>
          </>
      }
    </div>
  )
}
