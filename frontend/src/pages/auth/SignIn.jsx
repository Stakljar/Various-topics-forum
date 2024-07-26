import { useContext, useEffect, useState } from "react"
import Spinner from "../../components/Spinner"
import { Link, useLocation, useNavigate } from "react-router-dom"
import EmailAndPasswordInput from "../../components/EmailAndPasswordInput"
import axiosInstance from "../../axios/axiosInstances"
import { AuthContext } from "../../App"
import InfoMessage from "../../components/InfoMessage"
import Button from "../../components/Button"
import useErrorMessage from "../../hooks/useErrorMessage"

export default function SignIn() {
  const { setUser } = useContext(AuthContext)
  const [errorMessage, handleError, resetError, setErrorMessage] = useErrorMessage()
  const location = useLocation()
  const previousLocation = location.state?.previousLocation?.pathname || "/"
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)
  const [credentials, setCredentials] = useState({ email: "", password: "" })

  useEffect(() => {
    if (!isLoading) {
      return
    }
    const abortController = new AbortController()
    resetError()
    const signup = async () => {
      try {
        const response = await axiosInstance.post("/auth/signin",
          credentials,
          {
            signal: abortController.signal,
          })
        setUser({ id: response.data.id, role: response.data.role, accessToken: response.data.accessToken })
        navigate(previousLocation, { replace: true })
      }
      catch (error) {
        setIsLoading(false)
        if (error.response?.data === "User is disabled") {
          navigate(`/signin/signin-notice?email=${credentials.email}`)
        }
        else if (error.response?.data?.message === "Bad credentials") {
          setErrorMessage("Invalid username or password")
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
    <div id="signin">
      <h1 className="title">Topics Unraveled</h1>
      <hr />
      <div>
        Welcome to the forum where you can discuss about various topics.
        Here you can post discussions on certain topics but you can also upvote, downvote and comment on others' discussions.
      </div>
      <form onSubmit={(e) => { e.preventDefault(); setIsLoading(true) }}>
        <EmailAndPasswordInput credentials={credentials} setCredentials={setCredentials} type="signin" />
        <div>
          <Button text="Sign in" type="submit" />
        </div>
      </form>
      {isLoading && <Spinner />}
      {errorMessage && <InfoMessage infoMessage={errorMessage} />}
      <div id="signin-additional-options">
        <div>
          <span>Don't have an account?</span>
          <Link to="/signup"><Button text="Sign up" /></Link>
        </div>
        <div>
          <span>Lost account?</span>
          <Link to="/reset-password"><Button text="Reset password" /></Link>
        </div>
      </div>
    </div>
  )
}
